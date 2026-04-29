package br.voke.bdd.steps;

import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import br.voke.dominio.fidelidade.carteira.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciarCarteiraSteps {
    private final ContextoFidelidade ctx;

    public GerenciarCarteiraSteps(ContextoFidelidade ctx) {
        this.ctx = ctx;
    }

    @E("o total inserido no dia não atingiu o limite diário")
    public void totalNaoAtingiuLimiteDiario() { /* carteira nova */ }

    @Quando("ele adiciona um valor dentro do limite permitido")
    public void eleAdicionaValorPermitido() {
        try { ctx.carteira.adicionarSaldo(new BigDecimal("100.00")); } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("o saldo é creditado na carteira com sucesso")
    public void oSaldoECreditado() { assertNull(ctx.excecao); assertEquals(0, new BigDecimal("100.00").compareTo(ctx.carteira.getSaldo())); }

    @E("o total inserido no dia já atingiu o limite diário")
    public void totalAtingiuLimiteDiario() {
        ctx.carteira.adicionarSaldo(new BigDecimal("5000.00"));
    }

    @Quando("ele tenta adicionar mais saldo")
    public void eleTentaAdicionarMaisSaldo() {
        try { ctx.carteira.adicionarSaldo(new BigDecimal("600.00")); } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("o sistema rejeita a operação")
    public void oSistemaRejeitaOperacao() { assertNotNull(ctx.excecao); }

    @E("o saldo disponível na carteira é suficiente")
    public void saldoSuficiente() { ctx.carteira.adicionarSaldo(new BigDecimal("200.00")); }

    @E("o valor a remover está dentro do limite de remoção")
    public void valorDentroDoLimiteDeRemocao() { /* contexto */ }

    @Quando("ele solicita a remoção de saldo")
    public void eleSolicitaRemocao() {
        try { ctx.carteira.removerSaldo(new BigDecimal("50.00")); } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("o valor é debitado da carteira com sucesso")
    public void oValorEDebitado() { assertNull(ctx.excecao); assertTrue(ctx.carteira.getSaldo().compareTo(BigDecimal.ZERO) > 0); }

    @Quando("ele tenta remover um valor acima do limite de remoção permitido")
    public void eleTentaRemoverAcimaDoLimite() {
        try { ctx.carteira.removerSaldo(new BigDecimal("99999.00")); } catch (Exception e) { ctx.excecao = e; }
    }

    @E("o saldo disponível na carteira é insuficiente para o valor solicitado")
    public void saldoInsuficiente() { /* carteira recém-criada tem saldo 0 */ }

    @Quando("ele tenta remover saldo")
    public void eleTentaRemoverSaldo() {
        try { ctx.carteira.removerSaldo(new BigDecimal("50.00")); } catch (Exception e) { ctx.excecao = e; }
    }

}