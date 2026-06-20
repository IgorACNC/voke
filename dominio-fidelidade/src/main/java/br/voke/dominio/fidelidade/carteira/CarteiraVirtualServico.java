package br.voke.dominio.fidelidade.carteira;

import br.voke.dominio.fidelidade.excecao.LimiteFrequenciaSaqueException;
import br.voke.dominio.fidelidade.transacao.TipoTransacao;
import br.voke.dominio.fidelidade.transacao.TransacaoFinanceira;
import br.voke.dominio.fidelidade.transacao.TransacaoFinanceiraId;
import br.voke.dominio.fidelidade.transacao.TransacaoFinanceiraRepositorio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class CarteiraVirtualServico {

    private final CarteiraVirtualRepositorio repositorio;
    private final TransacaoFinanceiraRepositorio transacaoRepositorio;

    public CarteiraVirtualServico(CarteiraVirtualRepositorio repositorio,
                                  TransacaoFinanceiraRepositorio transacaoRepositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        Objects.requireNonNull(transacaoRepositorio, "Repositório de transações é obrigatório");
        this.repositorio = repositorio;
        this.transacaoRepositorio = transacaoRepositorio;
    }

    public CarteiraVirtual obterOuCriar(UUID participanteId) {
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        return repositorio.buscarPorParticipanteId(participanteId)
                .orElseGet(() -> {
                    CarteiraVirtual nova = new CarteiraVirtual(CarteiraVirtualId.novo(), participanteId);
                    repositorio.salvar(nova);
                    return nova;
                });
    }

    public void adicionarSaldo(UUID participanteId, BigDecimal valor) {
        adicionarSaldo(participanteId, valor, new InsercaoSaldoPadrao());
    }

    public void adicionarSaldo(UUID participanteId, BigDecimal valor, EstrategiaInsercaoSaldo estrategia) {
        Objects.requireNonNull(estrategia, "Estratégia de inserção é obrigatória");
        CarteiraVirtual carteira = obterOuCriar(participanteId);
        // RN1 - janela móvel real de 24h consultando histórico de transações,
        // independente do contador interno (que se reseta no virar do dia).
        BigDecimal totalUltimas24h = totalDepositosUltimas24h(participanteId);
        estrategia.validar(totalUltimas24h, valor);
        carteira.adicionarSaldo(valor, estrategia);
        repositorio.salvar(carteira);
        registrar(participanteId, TipoTransacao.DEPOSITO, valor, "Depósito");
    }

    public void removerSaldo(UUID participanteId, BigDecimal valor) {
        CarteiraVirtual carteira = obterOuCriar(participanteId);
        // RN2 - frequência verificada na janela móvel de 24h via banco.
        int saquesUltimas24h = saquesUltimas24h(participanteId);
        if (saquesUltimas24h >= 1) {
            throw new LimiteFrequenciaSaqueException();
        }
        carteira.removerSaldo(valor);
        repositorio.salvar(carteira);
        registrar(participanteId, TipoTransacao.SAQUE, valor, "Saque");
    }

    private BigDecimal totalDepositosUltimas24h(UUID participanteId) {
        BigDecimal total = transacaoRepositorio.somarDepositosDesde(
                participanteId, LocalDateTime.now().minusHours(24));
        return total != null ? total : BigDecimal.ZERO;
    }

    private int saquesUltimas24h(UUID participanteId) {
        return transacaoRepositorio.contarSaquesDesde(
                participanteId, LocalDateTime.now().minusHours(24));
    }

    public void debitar(UUID participanteId, BigDecimal valor) {
        CarteiraVirtual carteira = obterOuCriar(participanteId);
        carteira.debitar(valor);
        repositorio.salvar(carteira);
        registrar(participanteId, TipoTransacao.DEBITO_COMPRA, valor, "Compra de ingresso");
    }

    public void creditar(UUID participanteId, BigDecimal valor) {
        CarteiraVirtual carteira = obterOuCriar(participanteId);
        carteira.creditar(valor);
        repositorio.salvar(carteira);
        registrar(participanteId, TipoTransacao.CREDITO_BONUS, valor, "Crédito bônus");
    }

    public void estornar(UUID participanteId, BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) return;
        CarteiraVirtual carteira = obterOuCriar(participanteId);
        // RN3 - estorno devolve para o saldo real (sacável).
        carteira.creditarReembolso(valor);
        repositorio.salvar(carteira);
        registrar(participanteId, TipoTransacao.ESTORNO, valor, "Estorno de inscrição cancelada");
    }

    /** Retorna o saldo total disponível para compra (real + promocional). */
    public BigDecimal consultarSaldo(UUID participanteId) {
        return obterOuCriar(participanteId).getSaldoTotal();
    }

    /** Saldo real sacável. */
    public BigDecimal consultarSaldoReal(UUID participanteId) {
        return obterOuCriar(participanteId).getSaldo();
    }

    /** Saldo promocional (não sacável). */
    public BigDecimal consultarSaldoPromocional(UUID participanteId) {
        return obterOuCriar(participanteId).getSaldoPromocional();
    }

    public void resetarLimitesDiarios() {
        repositorio.resetarLimitesDiarios();
    }

    private void registrar(UUID participanteId, TipoTransacao tipo, BigDecimal valor, String descricao) {
        transacaoRepositorio.salvar(
                new TransacaoFinanceira(TransacaoFinanceiraId.novo(), participanteId, tipo, valor, descricao));
    }
}
