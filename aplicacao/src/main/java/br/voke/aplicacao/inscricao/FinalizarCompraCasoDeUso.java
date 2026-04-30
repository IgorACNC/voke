package br.voke.aplicacao.inscricao;

import br.voke.dominio.inscricao.carrinho.CarrinhoServico;
import br.voke.dominio.inscricao.carrinho.MetodoPagamento;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class FinalizarCompraCasoDeUso {

    private final CarrinhoServico servico;

    public FinalizarCompraCasoDeUso(CarrinhoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public BigDecimal executar(UUID participanteId, MetodoPagamento metodoPagamento) {
        BigDecimal total = servico.calcularTotal(participanteId, metodoPagamento);
        servico.limpar(participanteId);
        return total;
    }
}