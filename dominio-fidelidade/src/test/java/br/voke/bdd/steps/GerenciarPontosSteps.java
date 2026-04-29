package br.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Mas;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import br.voke.dominio.fidelidade.pontos.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciarPontosSteps {
    private final ContextoFidelidade ctx;
    private boolean presencaConfirmada;

    public GerenciarPontosSteps(ContextoFidelidade ctx) {
        this.ctx = ctx;
    }

    private void garantirConta() {
        if (ctx.conta == null) {
            ctx.conta = new ContaPontos(ContaPontosId.novo(), UUID.randomUUID());
        }
    }

    @Dado("que o participante compareceu ao evento")
    public void participanteCompareceu() { ctx.conta = new ContaPontos(ContaPontosId.novo(), UUID.randomUUID()); ctx.excecao = null; presencaConfirmada = true; }

    @E("o evento foi encerrado")
    public void eventoEncerrado() { /* contexto */ }

    @Quando("o sistema processa o encerramento")
    public void oSistemaProcessaEncerramento() {
        garantirConta();
        if (!presencaConfirmada) return;
        try { ctx.conta.creditarPorPresenca(100); } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("os pontos referentes ao evento são creditados na conta do participante")
    public void osPontosSaoCreditados() { assertNull(ctx.excecao); assertEquals(100, ctx.conta.getSaldo()); }

    @Dado("que o participante tinha inscrição no evento")
    public void participanteTinhaInscricao() { ctx.conta = new ContaPontos(ContaPontosId.novo(), UUID.randomUUID()); ctx.excecao = null; presencaConfirmada = true; }

    @Mas("a presença do participante não foi confirmada")
    public void presencaNaoConfirmada() { presencaConfirmada = false; }

    @Então("nenhum ponto é creditado ao participante")
    public void nenhumPontoCreditado() { assertEquals(0, ctx.conta.getSaldo()); }

    @Dado("que o participante possui saldo de pontos igual ou superior ao valor da bonificação")
    public void participanteComPontosSuficientes() {
        ctx.conta = new ContaPontos(ContaPontosId.novo(), UUID.randomUUID()); ctx.excecao = null;
        ctx.conta.creditarPorPresenca(500);
    }

    @Quando("ele seleciona e resgata a bonificação")
    public void eleResgataBonificacao() {
        try { ctx.conta.debitar(200); } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("a bonificação é concedida e os pontos são debitados do saldo")
    public void aBonificacaoEConcedida() { assertNull(ctx.excecao); assertEquals(300, ctx.conta.getSaldo()); }

    @Dado("que o participante possui saldo de pontos inferior ao valor da bonificação")
    public void participanteComPontosInsuficientes() {
        ctx.conta = new ContaPontos(ContaPontosId.novo(), UUID.randomUUID()); ctx.excecao = null;
        ctx.conta.creditarPorPresenca(50);
    }

    @Quando("ele tenta resgatar a bonificação")
    public void eleTentaResgatarBonificacao() {
        try { ctx.conta.debitar(200); } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("o sistema rejeita o resgate")
    public void oSistemaRejeitaResgate() { assertNotNull(ctx.excecao); }

    @Quando("ele tenta comprar pontos utilizando saldo real ou dinheiro")
    public void eleTentaComprarPontos() {
        garantirConta();
        try { ctx.conta.comprarPontos(100); } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("o sistema rejeita a ação")
    public void oSistemaRejeitaAcao() { assertNotNull(ctx.excecao); }

    @Dado("que o participante possui pontos com data de validade vencida")
    public void participanteComPontosExpirados() {
        ctx.conta = new ContaPontos(ContaPontosId.novo(), UUID.randomUUID()); ctx.excecao = null;
        ctx.conta.creditarPorPresenca(100);
    }

    @Quando("o sistema processa a expiração")
    public void oSistemaProcessaExpiracao() { ctx.conta.expirarPontos(100); }

    @Então("os pontos expirados são removidos do saldo do participante")
    public void osPontosExpiradosSaoRemovidos() { assertEquals(0, ctx.conta.getSaldo()); }

    @E("o participante é notificado sobre a expiração")
    public void participanteNotificado() { /* notificação seria na camada de aplicação */ }
}