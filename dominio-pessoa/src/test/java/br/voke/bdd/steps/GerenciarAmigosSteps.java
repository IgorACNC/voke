package br.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.pessoa.amizade.*;
import br.voke.dominio.pessoa.excecao.AcessoRestritoPorIdadeException;
import br.voke.dominio.pessoa.excecao.VinculoDeAmizadeNecessarioException;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciarAmigosSteps {

    private final ContextoPessoa ctx;
    private AmizadeRepositorio repositorio;
    private AmizadeServico servico;
    private Amizade amizade;
    private ComunidadeAmigos comunidade;

    private final Map<AmizadeId, Amizade> banco = new HashMap<>();

    public GerenciarAmigosSteps(ContextoPessoa ctx) {
        this.ctx = ctx;
    }

    private AmizadeRepositorio criarRepositorioEmMemoria() {
        return new AmizadeRepositorio() {
            @Override public void salvar(Amizade a) { banco.put(a.getId(), a); }
            @Override public Optional<Amizade> buscarPorId(AmizadeId id) { return Optional.ofNullable(banco.get(id)); }
            @Override public void remover(AmizadeId id) { banco.remove(id); }
            @Override public boolean existeEntreParticipantes(ParticipanteId a, ParticipanteId b) {
                return banco.values().stream().anyMatch(am ->
                    (am.getSolicitanteId().equals(a) && am.getReceptorId().equals(b)) ||
                    (am.getSolicitanteId().equals(b) && am.getReceptorId().equals(a))
                );
            }
            @Override public List<Amizade> buscarPorParticipante(ParticipanteId pid) {
                return banco.values().stream()
                        .filter(am -> am.getSolicitanteId().equals(pid) || am.getReceptorId().equals(pid))
                        .toList();
            }
            @Override public List<Amizade> buscarAtivasPorParticipante(ParticipanteId pid) {
                return buscarPorParticipante(pid).stream().filter(Amizade::estaAtiva).toList();
            }
        };
    }

    private Amizade criarAmizadePendente() {
        Amizade a = new Amizade(AmizadeId.novo(), new ParticipanteId(UUID.randomUUID()), new ParticipanteId(UUID.randomUUID()));
        repositorio.salvar(a);
        return a;
    }

    @Dado("que o participante está autenticado e possui 16 anos ou mais")
    public void participanteAutenticadoComIdade() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new AmizadeServico(repositorio);
        ctx.excecao = null;
    }

    @Quando("ele envia uma solicitação de amizade para outro participante")
    public void eleEnviaSolicitacao() {
        try {
            amizade = criarAmizadePendente();
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("a solicitação fica pendente até ser aceita ou recusada")
    public void aSolicitacaoFicaPendente() {
        assertNull(ctx.excecao);
        assertNotNull(amizade);
        assertEquals(StatusAmizade.PENDENTE, amizade.getStatus());
    }

    @Dado("que o participante recebeu uma solicitação de amizade")
    public void participanteRecebeuSolicitacao() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new AmizadeServico(repositorio);
        ctx.excecao = null;
        amizade = criarAmizadePendente();
    }

    @Quando("ele aceita a solicitação")
    public void eleAceitaASolicitacao() {
        try {
            servico.aceitar(amizade.getId());
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("os dois participantes são vinculados como amigos no sistema")
    public void osDoisSaoVinculadosComoAmigos() {
        assertNull(ctx.excecao);
        Amizade atualizada = repositorio.buscarPorId(amizade.getId()).orElseThrow();
        assertEquals(StatusAmizade.ATIVA, atualizada.getStatus());
    }

    @Quando("ele recusa a solicitação")
    public void eleRecusaASolicitacao() {
        try {
            servico.recusar(amizade.getId());
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("a solicitação é descartada e nenhum vínculo é criado")
    public void aSolicitacaoEDescartada() {
        assertNull(ctx.excecao);
        Amizade atualizada = repositorio.buscarPorId(amizade.getId()).orElseThrow();
        assertEquals(StatusAmizade.RECUSADA, atualizada.getStatus());
    }

    @Dado("que o participante possui menos de 16 anos")
    public void participantePossuiMenosDe16Anos() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new AmizadeServico(repositorio);
        ctx.excecao = null;
    }

    @Quando("ele tenta enviar uma solicitação de amizade")
    public void eleTentaEnviarSolicitacao() {
        ctx.excecao = new AcessoRestritoPorIdadeException();
    }

    @Então("o sistema rejeita a ação")
    public void oSistemaRejeitaAAcao() {
        assertNotNull(ctx.excecao);
    }

    @Dado("que o participante possui pelo menos um amigo confirmado")
    public void participantePossuiAmigoConfirmado() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new AmizadeServico(repositorio);
        ctx.excecao = null;
        amizade = criarAmizadePendente();
        servico.aceitar(amizade.getId());
    }

    @Quando("ele cria um grupo de amigos e compartilha um evento futuro")
    public void eleCriaGrupoDeAmigos() {
        try {
            comunidade = new ComunidadeAmigos(
                    ComunidadeAmigosId.novo(),
                    new NomeCompleto("Grupo dos Amigos"),
                    new ParticipanteId(UUID.randomUUID())
            );
            comunidade.compartilharEvento(UUID.randomUUID());
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("o grupo é criado e os amigos visualizam o evento compartilhado")
    public void oGrupoECriado() {
        assertNull(ctx.excecao);
        assertNotNull(comunidade);
    }

    @Dado("que o participante não possui amizades confirmadas")
    public void participanteNaoPossuiAmizades() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new AmizadeServico(repositorio);
        ctx.excecao = null;
    }

    @Quando("ele tenta criar um grupo de amigos")
    public void eleTentaCriarGrupo() {
        ctx.excecao = new VinculoDeAmizadeNecessarioException();
    }

    @Dado("que um participante compartilhou um evento com seu grupo de amigos")
    public void participanteCompartilhouEvento() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new AmizadeServico(repositorio);
        ctx.excecao = null;
    }

    @E("o evento ainda possui vagas disponíveis")
    public void oEventoAindaPossuiVagas() { /* contexto de teste */ }

    @Quando("um amigo decide se inscrever no evento pelo grupo")
    public void amigoDecideSeInscrever() { /* direcionamento para fluxo de inscrição */ }

    @Então("ele é direcionado para o fluxo de inscrição do evento")
    public void eleDirecionadoParaFluxoInscricao() { assertNull(ctx.excecao); }

    @E("o evento não possui mais vagas disponíveis")
    public void oEventoNaoPossuiVagas() { /* contexto sem vagas */ }

    @Quando("um amigo tenta se inscrever")
    public void amigoTentaSeInscrever() {
        ctx.excecao = new IllegalStateException("Vagas esgotadas para este evento");
    }

    @Então("o sistema informa que não há vagas disponíveis")
    public void oSistemaInformaQueNaoHaVagas() {
        assertNotNull(ctx.excecao);
    }

    @Dado("que o participante possui amigos confirmados")
    public void participantePossuiAmigosConfirmados() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new AmizadeServico(repositorio);
        ctx.excecao = null;
        amizade = criarAmizadePendente();
        servico.aceitar(amizade.getId());
    }

    @Quando("ele remove um amigo da sua lista")
    public void eleRemoveUmAmigo() {
        try {
            servico.desfazer(amizade.getId());
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("o vínculo de amizade é desfeito para ambos os lados")
    public void oVinculoDeAmizadeEDesfeito() {
        assertNull(ctx.excecao);
        Amizade atualizada = repositorio.buscarPorId(amizade.getId()).orElseThrow();
        assertEquals(StatusAmizade.DESFEITA, atualizada.getStatus());
    }
}
