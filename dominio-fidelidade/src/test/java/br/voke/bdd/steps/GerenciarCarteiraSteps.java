package br.voke.bdd.steps;

import br.voke.dominio.fidelidade.carteira.CarteiraVirtual;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualId;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualRepositorio;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualServico;
import br.voke.dominio.fidelidade.transacao.TransacaoFinanceiraRepositorio;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.math.BigDecimal;
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

public class GerenciarCarteiraSteps {
    private final ContextoFidelidade ctx;
    private final CarteiraVirtualRepositorio repositorio = criarRepositorioEmMemoria();
    private final TransacaoFinanceiraRepositorio transacaoRepositorio = mock(TransacaoFinanceiraRepositorio.class);
    private final CarteiraVirtualServico servico = new CarteiraVirtualServico(repositorio, transacaoRepositorio);
    private UUID participanteId;

    public GerenciarCarteiraSteps(ContextoFidelidade ctx) {
        this.ctx = ctx;
    }

    private CarteiraVirtualRepositorio criarRepositorioEmMemoria() {
        CarteiraVirtualRepositorio repo = mock(CarteiraVirtualRepositorio.class);
        Map<CarteiraVirtualId, CarteiraVirtual> carteiras = new HashMap<>();

        doAnswer(invocation -> {
            CarteiraVirtual carteira = invocation.getArgument(0);
            carteiras.put(carteira.getId(), carteira);
            ctx.carteira = carteira;
            participanteId = carteira.getParticipanteId();
            return null;
        }).when(repo).salvar(any(CarteiraVirtual.class));

        doAnswer(invocation -> Optional.ofNullable(carteiras.get(invocation.getArgument(0))))
                .when(repo).buscarPorId(any(CarteiraVirtualId.class));

        doAnswer(invocation -> {
            UUID id = invocation.getArgument(0);
            return carteiras.values().stream()
                    .filter(carteira -> carteira.getParticipanteId().equals(id))
                    .findFirst();
        }).when(repo).buscarPorParticipanteId(any(UUID.class));

        return repo;
    }

    private UUID participanteAtual() {
        if (participanteId != null) {
            return participanteId;
        }
        if (ctx.carteira != null) {
            repositorio.salvar(ctx.carteira);
            return ctx.carteira.getParticipanteId();
        }
        participanteId = UUID.randomUUID();
        ctx.carteira = servico.obterOuCriar(participanteId);
        return participanteId;
    }

    @E("o total inserido no dia não atingiu o limite diário")
    public void totalNaoAtingiuLimiteDiario() {
        participanteAtual();
    }

    @Quando("ele adiciona um valor dentro do limite permitido")
    public void eleAdicionaValorPermitido() {
        try {
            servico.adicionarSaldo(participanteAtual(), new BigDecimal("100.00"));
        } catch (Exception e) {
            ctx.excecao = e;
        }
    }

    @Então("o saldo é creditado na carteira com sucesso")
    public void oSaldoECreditado() {
        assertNull(ctx.excecao);
        assertTrue(new BigDecimal("100.00").compareTo(ctx.carteira.getSaldo()) == 0);
        verify(repositorio, atLeastOnce()).salvar(ctx.carteira);
    }

    @E("o total inserido no dia já atingiu o limite diário")
    public void totalAtingiuLimiteDiario() {
        servico.adicionarSaldo(participanteAtual(), new BigDecimal("5000.00"));
    }

    @Quando("ele tenta adicionar mais saldo")
    public void eleTentaAdicionarMaisSaldo() {
        try {
            servico.adicionarSaldo(participanteAtual(), new BigDecimal("600.00"));
        } catch (Exception e) {
            ctx.excecao = e;
        }
    }

    @Então("o sistema rejeita a operação")
    public void oSistemaRejeitaOperacao() {
        assertNotNull(ctx.excecao);
    }

    @E("o saldo disponível na carteira é suficiente")
    public void saldoSuficiente() {
        servico.adicionarSaldo(participanteAtual(), new BigDecimal("200.00"));
    }

    @E("o valor a remover está dentro do limite de remoção")
    public void valorDentroDoLimiteDeRemocao() {
        assertTrue(new BigDecimal("50.00").compareTo(new BigDecimal("500.00")) <= 0);
    }

    @Quando("ele solicita a remoção de saldo")
    public void eleSolicitaRemocao() {
        try {
            servico.removerSaldo(participanteAtual(), new BigDecimal("50.00"));
        } catch (Exception e) {
            ctx.excecao = e;
        }
    }

    @Então("o valor é debitado da carteira com sucesso")
    public void oValorEDebitado() {
        assertNull(ctx.excecao);
        assertTrue(ctx.carteira.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        verify(repositorio, atLeastOnce()).salvar(ctx.carteira);
    }

    @Quando("ele tenta remover um valor acima do limite de remoção permitido")
    public void eleTentaRemoverAcimaDoLimite() {
        try {
            servico.removerSaldo(participanteAtual(), new BigDecimal("99999.00"));
        } catch (Exception e) {
            ctx.excecao = e;
        }
    }

    @E("o saldo disponível na carteira é insuficiente para o valor solicitado")
    public void saldoInsuficiente() {
        participanteAtual();
    }

    @Quando("ele tenta remover saldo")
    public void eleTentaRemoverSaldo() {
        try {
            servico.removerSaldo(participanteAtual(), new BigDecimal("50.00"));
        } catch (Exception e) {
            ctx.excecao = e;
        }
    }

    @Dado("que o participante possui saldo suficiente na carteira")
    public void participantePossuiSaldoSuficienteNaCarteira() {
        participanteAtual();
        servico.creditar(participanteAtual(), new BigDecimal("1000.00"));
        ctx.excecao = null;
    }

    @Quando("o sistema processa o pagamento de uma compra cujo valor excede o limite individual de remoção")
    public void sistemaProcessaPagamentoAcimaDoLimiteIndividualDeRemocao() {
        try {
            servico.debitar(participanteAtual(), new BigDecimal("750.00"));
        } catch (Exception e) {
            ctx.excecao = e;
        }
    }

    @Então("o saldo é debitado com sucesso")
    public void saldoDebitadoComSucesso() {
        assertNull(ctx.excecao);
        assertEquals(0, new BigDecimal("250.00").compareTo(ctx.carteira.getSaldo()));
        verify(repositorio, atLeastOnce()).salvar(ctx.carteira);
    }

    @E("nenhum erro de limite de remoção é lançado")
    public void nenhumErroDeLimiteDeRemocaoLancado() {
        assertNull(ctx.excecao);
    }

    @Dado("que o participante atingiu o limite diário de inserção de saldo")
    public void participanteAtingiuLimiteDiarioDeInsercaoDeSaldo() {
        servico.adicionarSaldo(participanteAtual(), new BigDecimal("5000.00"));
        ctx.excecao = null;
    }

    @Quando("o sistema processa a virada do dia")
    public void sistemaProcessaViradaDoDia() {
        ctx.carteira.resetarLimiteDiario();
        repositorio.salvar(ctx.carteira);
    }

    @Então("o contador de inserção diária é reiniciado")
    public void contadorDeInsercaoDiariaReiniciado() {
        assertEquals(0, BigDecimal.ZERO.compareTo(ctx.carteira.getTotalInseridoHoje()));
        verify(repositorio, atLeastOnce()).salvar(ctx.carteira);
    }

    @E("o participante pode voltar a adicionar saldo normalmente")
    public void participantePodeVoltarAAdicionarSaldoNormalmente() {
        try {
            servico.adicionarSaldo(participanteAtual(), new BigDecimal("100.00"));
        } catch (Exception e) {
            ctx.excecao = e;
        }
        assertNull(ctx.excecao);
        assertEquals(0, new BigDecimal("5100.00").compareTo(ctx.carteira.getSaldo()));
    }
}