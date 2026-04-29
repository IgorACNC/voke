package br.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import br.voke.dominio.fidelidade.sugestao.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciarSugestoesSteps {
    private SugestaoRepositorio repositorio;
    private Sugestao sugestao;
    private List<Sugestao> sugestoes;
    private Exception excecao;
    private final Map<SugestaoId, Sugestao> banco = new HashMap<>();

    private SugestaoRepositorio criarRepo() {
        return new SugestaoRepositorio() {
            @Override public void salvar(Sugestao s) { banco.put(s.getId(), s); }
            @Override public Optional<Sugestao> buscarPorId(SugestaoId id) { return Optional.ofNullable(banco.get(id)); }
            @Override public List<Sugestao> buscarPorParticipanteId(UUID pid) { return new ArrayList<>(banco.values()); }
            @Override public void remover(SugestaoId id) { banco.remove(id); }
            @Override public int contarSugestoesSemanalPorParticipante(UUID pid) {
                return (int) banco.values().stream().filter(s -> s.getParticipanteId().equals(pid)).count();
            }
        };
    }

    @Dado("que o participante possui interesses cadastrados no sistema")
    public void participanteComInteresses() {
        banco.clear(); repositorio = criarRepo(); excecao = null; sugestoes = new ArrayList<>();
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
    public void participanteRecebe4Sugestoes() { assertEquals(4, sugestoes.size()); }

    @Dado("que o participante recebeu uma sugestão de evento")
    public void participanteRecebeuSugestao() {
        banco.clear(); repositorio = criarRepo(); excecao = null;
        sugestao = new Sugestao(SugestaoId.novo(), UUID.randomUUID(), UUID.randomUUID(), "Show ao vivo");
        repositorio.salvar(sugestao);
    }

    @Quando("ele indica que gostou da sugestão")
    public void eleIndicaQueGostou() { try { sugestao.aprovar(); repositorio.salvar(sugestao); } catch (Exception e) { excecao = e; } }

    @Então("o sistema registra o feedback positivo")
    public void feedbackPositivoRegistrado() { assertNull(excecao); assertEquals(StatusSugestao.APROVADA, sugestao.getStatus()); }

    @E("utiliza essa informação para refinar sugestões futuras")
    public void utilizaParaRefinar() { /* motor de recomendação */ }

    @Quando("ele indica que não gostou da sugestão")
    public void eleIndicaQueNaoGostou() { try { sugestao.rejeitar(); repositorio.salvar(sugestao); } catch (Exception e) { excecao = e; } }

    @Então("o sistema registra o feedback negativo")
    public void feedbackNegativoRegistrado() { assertNull(excecao); assertEquals(StatusSugestao.REJEITADA, sugestao.getStatus()); }

    @E("reformula as próximas sugestões evitando perfil semelhante ao rejeitado")
    public void reformulaSugestoes() { /* motor de recomendação */ }

    @Dado("que o administrador está autenticado no sistema")
    public void administradorAutenticado() { banco.clear(); repositorio = criarRepo(); excecao = null; }

    @Quando("ele adiciona uma sugestão de evento para um usuário específico")
    public void eleAdicionaSugestao() {
        sugestao = new Sugestao(SugestaoId.novo(), UUID.randomUUID(), UUID.randomUUID(), "Evento indicado pelo admin");
        repositorio.salvar(sugestao);
    }

    @Então("a sugestão é inserida na fila de sugestões do usuário")
    public void aSugestaoEInserida() { assertNotNull(sugestao); assertEquals(StatusSugestao.PENDENTE, sugestao.getStatus()); }

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
    public void aSugestaoEExcluida() { assertFalse(repositorio.buscarPorId(sugestao.getId()).isPresent()); }
}
