package br.voke.dominio.fidelidade.carteira;

import br.voke.dominio.fidelidade.transacao.TipoTransacao;
import br.voke.dominio.fidelidade.transacao.TransacaoFinanceira;
import br.voke.dominio.fidelidade.transacao.TransacaoFinanceiraId;
import br.voke.dominio.fidelidade.transacao.TransacaoFinanceiraRepositorio;

import java.math.BigDecimal;
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
        carteira.adicionarSaldo(valor, estrategia);
        repositorio.salvar(carteira);
        registrar(participanteId, TipoTransacao.DEPOSITO, valor, "Depósito");
    }

    public void removerSaldo(UUID participanteId, BigDecimal valor) {
        CarteiraVirtual carteira = obterOuCriar(participanteId);
        carteira.removerSaldo(valor);
        repositorio.salvar(carteira);
        registrar(participanteId, TipoTransacao.SAQUE, valor, "Saque");
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

    public BigDecimal consultarSaldo(UUID participanteId) {
        return obterOuCriar(participanteId).getSaldo();
    }

    public void resetarLimitesDiarios() {
        repositorio.resetarLimitesDiarios();
    }

    private void registrar(UUID participanteId, TipoTransacao tipo, BigDecimal valor, String descricao) {
        transacaoRepositorio.salvar(
                new TransacaoFinanceira(TransacaoFinanceiraId.novo(), participanteId, tipo, valor, descricao));
    }
}
