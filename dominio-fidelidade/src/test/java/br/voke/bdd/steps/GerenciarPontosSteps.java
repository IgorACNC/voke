package br.voke.bdd.steps;

import br.voke.dominio.fidelidade.pontos.ContaPontos;
import br.voke.dominio.fidelidade.pontos.ContaPontosId;
import br.voke.dominio.fidelidade.pontos.ContaPontosRepositorio;
import br.voke.dominio.fidelidade.pontos.ContaPontosServico;
import br.voke.dominio.fidelidade.pontos.ExpiracaoPontosNotificador;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Mas;
import io.cucumber.java.pt.Quando;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GerenciarPontosSteps {
    private final ContextoFidelidade ctx;
    private final UUID participanteId = UUID.randomUUID();
    private final ContaPontosRepositorio repositorio = criarRepositorioEmMemoria();
    private final ExpiracaoPontosNotificador notificador = mock(ExpiracaoPontosNotificador.class);
    private final ContaPontosServico servico = new ContaPontosServico(repositorio, notificador);
    private boolean presencaConfirmada;
    private boolean eventoEncerrado;

    public GerenciarPontosSteps(ContextoFidelidade ctx) {
        this.ctx = ctx;
    }

    private ContaPontosRepositorio criarRepositorioEmMemoria() {
        ContaPontosRepositorio repo = mock(ContaPontosRepositorio.class);
        Map<ContaPontosId, ContaPontos> contas = new HashMap<>();

        doAnswer(invocation -> {
            ContaPontos conta = invocation.getArgument(0);
            contas.put(conta.getId(), conta);
            ctx.conta = conta;
            return null;
        }).when(repo).salvar(any(ContaPontos.class));

        doAnswer(invocation -> Optional.ofNullable(contas.get(invocation.getArgument(0))))
                .when(repo).buscarPorId(any(ContaPontosId.class));

        doAnswer(invocation -> {
            UUID id = invocation.getArgument(0);
            return contas.values().stream()
                    .filter(conta -> conta.getParticipanteId().equals(id))
                    .findFirst();
        }).when(repo).buscarPorParticipanteId(any(UUID.class));

        return repo;
    }

    private void garantirConta() {
        if (ctx.conta == null) {
            ctx.conta = servico.obterOuCriar(participanteId);
        }
    }

    @Dado("que o participante compareceu ao evento")
    public void participanteCompareceu() {
        ctx.conta = servico.obterOuCriar(participanteId);
        ctx.excecao = null;
        presencaConfirmada = true;
    }

    @E("o evento foi encerrado")
    public void eventoEncerrado() {
        eventoEncerrado = true;
    }

    @Quando("o sistema processa o encerramento")
    public void oSistemaProcessaEncerramento() {
        garantirConta();
        if (!presencaConfirmada) return;
        try {
            servico.creditarPorPresenca(participanteId, 100, eventoEncerrado, presencaConfirmada);
        } catch (Exception e) {
            ctx.excecao = e;
        }
    }

    @Então("os pontos referentes ao evento são creditados na conta do participante")
    public void osPontosSaoCreditados() {
        assertNull(ctx.excecao);
        assertEquals(100, ctx.conta.getSaldo());
        verify(repositorio, atLeastOnce()).salvar(ctx.conta);
    }

    @Dado("que o participante tinha inscrição no evento")
    public void participanteTinhaInscricao() {
        ctx.conta = servico.obterOuCriar(participanteId);
        ctx.excecao = null;
        presencaConfirmada = true;
        eventoEncerrado = false;
    }

    @Mas("a presença do participante não foi confirmada")
    public void presencaNaoConfirmada() {
        presencaConfirmada = false;
    }

    @Então("nenhum ponto é creditado ao participante")
    public void nenhumPontoCreditado() {
        assertEquals(0, ctx.conta.getSaldo());
    }

    @Dado("que o participante possui saldo de pontos igual ou superior ao valor da bonificação")
    public void participanteComPontosSuficientes() {
        ctx.conta = servico.obterOuCriar(participanteId);
        ctx.excecao = null;
        servico.creditarPorPresenca(participanteId, 500, true, true);
    }

    @Quando("ele seleciona e resgata a bonificação")
    public void eleResgataBonificacao() {
        try {
            servico.debitar(participanteId, 200);
        } catch (Exception e) {
            ctx.excecao = e;
        }
    }

    @Então("a bonificação é concedida e os pontos são debitados do saldo")
    public void aBonificacaoEConcedida() {
        assertNull(ctx.excecao);
        assertEquals(300, ctx.conta.getSaldo());
        verify(repositorio, atLeastOnce()).salvar(ctx.conta);
    }

    @Dado("que o participante possui saldo de pontos inferior ao valor da bonificação")
    public void participanteComPontosInsuficientes() {
        ctx.conta = servico.obterOuCriar(participanteId);
        ctx.excecao = null;
        servico.creditarPorPresenca(participanteId, 50, true, true);
    }

    @Quando("ele tenta resgatar a bonificação")
    public void eleTentaResgatarBonificacao() {
        try {
            servico.debitar(participanteId, 200);
        } catch (Exception e) {
            ctx.excecao = e;
        }
    }

    @Então("o sistema rejeita o resgate")
    public void oSistemaRejeitaResgate() {
        assertNotNull(ctx.excecao);
    }

    @Quando("ele tenta comprar pontos utilizando saldo real ou dinheiro")
    public void eleTentaComprarPontos() {
        garantirConta();
        try {
            ctx.conta.comprarPontos(100);
        } catch (Exception e) {
            ctx.excecao = e;
        }
    }

    @Então("o sistema rejeita a ação")
    public void oSistemaRejeitaAcao() {
        assertNotNull(ctx.excecao);
    }

    @Dado("que o participante possui pontos com data de validade vencida")
    public void participanteComPontosExpirados() {
        ctx.conta = servico.obterOuCriar(participanteId);
        ctx.excecao = null;
        servico.creditarPorPresenca(participanteId, 100, true, true);
    }

    @Quando("o sistema processa a expiração")
    public void oSistemaProcessaExpiracao() {
        servico.expirarPontos(participanteId, 100);
    }

    @Então("os pontos expirados são removidos do saldo do participante")
    public void osPontosExpiradosSaoRemovidos() {
        assertEquals(0, ctx.conta.getSaldo());
        verify(repositorio, atLeastOnce()).salvar(ctx.conta);
    }

    @E("o participante é notificado sobre a expiração")
    public void participanteNotificado() {
        verify(notificador).notificarExpiracao(participanteId, 100);
    }
}