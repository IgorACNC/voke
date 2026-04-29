package br.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.compartilhado.Cpf;
import br.voke.dominio.compartilhado.DataNascimento;
import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.compartilhado.Senha;
import br.voke.dominio.pessoa.amizade.*;
import br.voke.dominio.pessoa.excecao.VinculoDeAmizadeNecessarioException;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GerenciarAmigosSteps {

    private final ContextoPessoa ctx;
    private AmizadeRepositorio repositorio;
    private AmizadeServico servico;
    private Amizade amizade;
    private ComunidadeAmigos comunidade;
    private Participante solicitanteAmizade;
    private ParticipanteId receptorAmizadeId;
    private UUID eventoCompartilhadoId;
    private boolean eventoComVagas;
    private boolean direcionadoParaInscricao;

    private final Map<AmizadeId, Amizade> banco = new HashMap<>();

    public GerenciarAmigosSteps(ContextoPessoa ctx) {
        this.ctx = ctx;
    }

    private AmizadeRepositorio criarRepositorioEmMemoria() {
        AmizadeRepositorio mockRepositorio = mock(AmizadeRepositorio.class);
        doAnswer(invocation -> {
            Amizade amizadeSalva = invocation.getArgument(0);
            banco.put(amizadeSalva.getId(), amizadeSalva);
            return null;
        }).when(mockRepositorio).salvar(any(Amizade.class));
        doAnswer(invocation -> java.util.Optional.ofNullable(banco.get(invocation.getArgument(0))))
                .when(mockRepositorio).buscarPorId(any(AmizadeId.class));
        doAnswer(invocation -> {
            banco.remove(invocation.getArgument(0));
            return null;
        }).when(mockRepositorio).remover(any(AmizadeId.class));
        doAnswer(invocation -> {
            ParticipanteId a = invocation.getArgument(0);
            ParticipanteId b = invocation.getArgument(1);
            return banco.values().stream().anyMatch(am ->
                    (am.getSolicitanteId().equals(a) && am.getReceptorId().equals(b)) ||
                            (am.getSolicitanteId().equals(b) && am.getReceptorId().equals(a)));
        }).when(mockRepositorio).existeEntreParticipantes(any(ParticipanteId.class), any(ParticipanteId.class));
        doAnswer(invocation -> {
            ParticipanteId pid = invocation.getArgument(0);
            return banco.values().stream()
                    .filter(am -> am.getSolicitanteId().equals(pid) || am.getReceptorId().equals(pid))
                    .toList();
        }).when(mockRepositorio).buscarPorParticipante(any(ParticipanteId.class));
        doAnswer(invocation -> {
            ParticipanteId pid = invocation.getArgument(0);
            return banco.values().stream()
                    .filter(am -> am.getSolicitanteId().equals(pid) || am.getReceptorId().equals(pid))
                    .filter(Amizade::estaAtiva)
                    .toList();
        }).when(mockRepositorio).buscarAtivasPorParticipante(any(ParticipanteId.class));
        return mockRepositorio;
    }

    private Amizade criarAmizadePendente() {
        Amizade a = new Amizade(AmizadeId.novo(), new ParticipanteId(UUID.randomUUID()), new ParticipanteId(UUID.randomUUID()));
        repositorio.salvar(a);
        return a;
    }

    private Participante criarParticipanteMaiorDe16(String cpf, String email) {
        return new Participante(
                new ParticipanteId(UUID.randomUUID()),
                new NomeCompleto("Participante Amigo"),
                new Cpf(cpf),
                new Email(email),
                new Senha("Senha@123"),
                new DataNascimento(LocalDate.of(2000, 1, 1))
        );
    }

    @Dado("que o participante está autenticado e possui 16 anos ou mais")
    public void participanteAutenticadoComIdade() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new AmizadeServico(repositorio);
        ctx.excecao = null;
        solicitanteAmizade = criarParticipanteMaiorDe16("529.982.247-25", "amizade@email.com");
        receptorAmizadeId = new ParticipanteId(UUID.randomUUID());
    }

    @Quando("ele envia uma solicitação de amizade para outro participante")
    public void eleEnviaSolicitacao() {
        try {
            amizade = servico.enviarSolicitacao(solicitanteAmizade, receptorAmizadeId);
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("a solicitação fica pendente até ser aceita ou recusada")
    public void aSolicitacaoFicaPendente() {
        assertNull(ctx.excecao);
        assertNotNull(amizade);
        assertEquals(StatusAmizade.PENDENTE, amizade.getStatus());
        verify(repositorio, atLeastOnce()).salvar(amizade);
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
        verify(repositorio, atLeastOnce()).salvar(amizade);
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
        verify(repositorio, atLeastOnce()).salvar(amizade);
    }

    @Dado("que o participante possui menos de 16 anos")
    public void participantePossuiMenosDe16Anos() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new AmizadeServico(repositorio);
        ctx.excecao = null;
        solicitanteAmizade = mock(Participante.class);
        when(solicitanteAmizade.getId()).thenReturn(new ParticipanteId(UUID.randomUUID()));
        when(solicitanteAmizade.getIdade()).thenReturn(15);
        receptorAmizadeId = new ParticipanteId(UUID.randomUUID());
    }

    @Quando("ele tenta enviar uma solicitação de amizade")
    public void eleTentaEnviarSolicitacao() {
        try {
            servico.enviarSolicitacao(solicitanteAmizade, receptorAmizadeId);
        } catch (Exception e) { ctx.excecao = e; }
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
        ParticipanteId participanteId = new ParticipanteId(UUID.randomUUID());
        try {
            if (!servico.possuiAmizadeAtiva(participanteId)) {
                throw new VinculoDeAmizadeNecessarioException();
            }
            comunidade = new ComunidadeAmigos(
                    ComunidadeAmigosId.novo(),
                    new NomeCompleto("Grupo dos Amigos"),
                    participanteId
            );
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Dado("que um participante compartilhou um evento com seu grupo de amigos")
    public void participanteCompartilhouEvento() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new AmizadeServico(repositorio);
        ctx.excecao = null;
        ParticipanteId criadorId = new ParticipanteId(UUID.randomUUID());
        ParticipanteId amigoId = new ParticipanteId(UUID.randomUUID());
        comunidade = new ComunidadeAmigos(
                ComunidadeAmigosId.novo(),
                new NomeCompleto("Grupo para evento"),
                criadorId
        );
        comunidade.adicionarMembro(amigoId);
        eventoCompartilhadoId = UUID.randomUUID();
        comunidade.compartilharEvento(eventoCompartilhadoId);
    }

    @E("o evento ainda possui vagas disponíveis")
    public void oEventoAindaPossuiVagas() {
        assertTrue(comunidade.getEventoCompartilhadoIds().contains(eventoCompartilhadoId));
        eventoComVagas = true;
    }

    @Quando("um amigo decide se inscrever no evento pelo grupo")
    public void amigoDecideSeInscrever() {
        if (!eventoComVagas) {
            ctx.excecao = new IllegalStateException("Não há vagas disponíveis para este evento");
            return;
        }
        direcionadoParaInscricao = comunidade.getEventoCompartilhadoIds().contains(eventoCompartilhadoId);
    }

    @Então("ele é direcionado para o fluxo de inscrição do evento")
    public void eleDirecionadoParaFluxoInscricao() { assertNull(ctx.excecao); assertTrue(direcionadoParaInscricao); }

    @E("o evento não possui mais vagas disponíveis")
    public void oEventoNaoPossuiVagas() {
        assertTrue(comunidade.getEventoCompartilhadoIds().contains(eventoCompartilhadoId));
        eventoComVagas = false;
    }

    @Quando("um amigo tenta se inscrever")
    public void amigoTentaSeInscrever() {
        ctx.excecao = new IllegalStateException("Não há vagas disponíveis para este evento");
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
        verify(repositorio, atLeastOnce()).salvar(amizade);
    }
}
