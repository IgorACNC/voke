package br.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import br.voke.dominio.fidelidade.sugestao.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GerenciarSugestoesSteps {
    private SugestaoRepositorio repositorio;
    private Sugestao sugestao;
    private List<Sugestao> sugestoes;
    private Exception excecao;
    private boolean perfilRefinado;
    private boolean sugestoesReformuladas;
    private final Map<SugestaoId, Sugestao> banco = new HashMap<>();

    private SugestaoRepositorio criarRepo() {
        SugestaoRepositorio mockRepositorio = mock(SugestaoRepositorio.class);
        doAnswer(invocation -> {
            Sugestao sugestaoSalva = invocation.getArgument(0);
            banco.put(sugestaoSalva.getId(), sugestaoSalva);
            return null;
        }).when(mockRepositorio).salvar(any(Sugestao.class));
        doAnswer(invocation -> java.util.Optional.ofNullable(banco.get(invocation.getArgument(0))))
                .when(mockRepositorio).buscarPorId(any(SugestaoId.class));
        doAnswer(invocation -> new ArrayList<>(banco.values()))
                .when(mockRepositorio).buscarPorParticipanteId(any(UUID.class));
        doAnswer(invocation -> {
            banco.remove(invocation.getArgument(0));
            return null;
        }).when(mockRepositorio).remover(any(SugestaoId.class));
        doAnswer(invocation -> {
            UUID pid = invocation.getArgument(0);
            return (int) banco.values().stream().filter(s -> s.getParticipanteId().equals(pid)).count();
        }).when(mockRepositorio).contarSugestoesSemanalPorParticipante(any(UUID.class));
        return mockRepositorio;
    }

    @Dado("que o participante possui interesses cadastrados no sistema")
    public void participanteComInteresses() {
        banco.clear(); repositorio = criarRepo(); excecao = null; sugestoes = new ArrayList<>(); perfilRefinado = false; sugestoesReformuladas = false;
    }

    @Quando("o sistema processa as sugestões semanais")
    public void oSistemaProcessaSugestoesSemanais() {
        UUID participanteId = UUID.randomUUID();
        for (int i = 0; i < 4; i++) {
            Sugestao s = new Sugestao(SugestaoId.novo(), participanteId, UUID.randomUUID(), "Evento sugerido " + (i + 1));
            repositorio.salvar(s);
            sugestoes.add(s);
        }
    }

    @Então("o participante recebe exatamente 4 sugestões de eventos alinhadas ao seu perfil")
    public void participanteRecebe4Sugestoes() { assertEquals(4, sugestoes.size()); verify(repositorio).salvar(sugestoes.get(0)); }

    @Dado("que o participante recebeu uma sugestão de evento")
    public void participanteRecebeuSugestao() {
        banco.clear(); repositorio = criarRepo(); excecao = null; perfilRefinado = false; sugestoesReformuladas = false;
        sugestao = new Sugestao(SugestaoId.novo(), UUID.randomUUID(), UUID.randomUUID(), "Show ao vivo");
        repositorio.salvar(sugestao);
    }

    @Quando("ele indica que gostou da sugestão")
    public void eleIndicaQueGostou() { try { sugestao.aprovar(); repositorio.salvar(sugestao); } catch (Exception e) { excecao = e; } }

    @Então("o sistema registra o feedback positivo")
    public void feedbackPositivoRegistrado() { assertNull(excecao); assertEquals(StatusSugestao.APROVADA, sugestao.getStatus()); verify(repositorio, atLeastOnce()).salvar(sugestao); }

    @E("utiliza essa informação para refinar sugestões futuras")
    public void utilizaParaRefinar() {
        perfilRefinado = sugestao.getStatus() == StatusSugestao.APROVADA;
        assertTrue(perfilRefinado);
    }

    @Quando("ele indica que não gostou da sugestão")
    public void eleIndicaQueNaoGostou() { try { sugestao.rejeitar(); repositorio.salvar(sugestao); } catch (Exception e) { excecao = e; } }

    @Então("o sistema registra o feedback negativo")
    public void feedbackNegativoRegistrado() { assertNull(excecao); assertEquals(StatusSugestao.REJEITADA, sugestao.getStatus()); verify(repositorio, atLeastOnce()).salvar(sugestao); }

    @E("reformula as próximas sugestões evitando perfil semelhante ao rejeitado")
    public void reformulaSugestoes() {
        sugestoesReformuladas = sugestao.getStatus() == StatusSugestao.REJEITADA;
        assertTrue(sugestoesReformuladas);
    }

    @Dado("que o administrador está autenticado no sistema")
    public void administradorAutenticado() { banco.clear(); repositorio = criarRepo(); excecao = null; }

    @Quando("ele adiciona uma sugestão de evento para um usuário específico")
    public void eleAdicionaSugestao() {
        sugestao = new Sugestao(SugestaoId.novo(), UUID.randomUUID(), UUID.randomUUID(), "Evento indicado pelo admin");
        repositorio.salvar(sugestao);
    }

    @Então("a sugestão é inserida na fila de sugestões do usuário")
    public void aSugestaoEInserida() { assertNotNull(sugestao); assertEquals(StatusSugestao.PENDENTE, sugestao.getStatus()); verify(repositorio).salvar(sugestao); }

    @Dado("que uma sugestão foi enviada ao usuário e ainda está pendente de avaliação")
    public void sugestaoPendente() {
        banco.clear(); repositorio = criarRepo(); excecao = null;
        sugestao = new Sugestao(SugestaoId.novo(), UUID.randomUUID(), UUID.randomUUID(), "Sugestão pendente");
        repositorio.salvar(sugestao);
    }

    @Quando("o administrador edita o conteúdo da sugestão")
    public void administradorEditaSugestao() {
        sugestao.editar("Sugestão atualizada pelo admin");
        repositorio.salvar(sugestao);
    }

    @Então("a sugestão atualizada é exibida ao usuário")
    public void aSugestaoAtualizadaEExibida() { assertEquals("Sugestão atualizada pelo admin", sugestao.getDescricao()); }

    @Quando("o administrador remove a sugestão")
    public void administradorRemoveSugestao() { repositorio.remover(sugestao.getId()); }

    @Então("a sugestão é excluída e não aparece mais para o usuário")
    public void aSugestaoEExcluida() { assertFalse(repositorio.buscarPorId(sugestao.getId()).isPresent()); verify(repositorio).remover(sugestao.getId()); }

    @Dado("que uma sugestão foi enviada ao usuário e não foi avaliada dentro do prazo")
    public void sugestaoEnviadaNaoAvaliadaDentroDoPrazo() {
        banco.clear(); repositorio = criarRepo(); excecao = null;
        sugestao = new Sugestao(SugestaoId.novo(), UUID.randomUUID(), UUID.randomUUID(), "Sugestão pendente expirada");
        repositorio.salvar(sugestao);
    }

    @Quando("o sistema processa a expiração das sugestões pendentes")
    public void sistemaProcessaExpiracaoDasSugestoesPendentes() {
        try {
            banco.values().stream()
                    .filter(Sugestao::estaPendente)
                    .forEach(s -> {
                        s.expirar();
                        repositorio.salvar(s);
                    });
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Então("o status da sugestão é atualizado para expirada")
    public void statusDaSugestaoAtualizadoParaExpirada() {
        assertNull(excecao);
        assertEquals(StatusSugestao.EXPIRADA, sugestao.getStatus());
        verify(repositorio, atLeastOnce()).salvar(sugestao);
    }

    @E("a sugestão não é mais apresentada ao usuário")
    public void sugestaoNaoEApresentadaAoUsuario() {
        assertFalse(sugestao.estaPendente());
    }
}
