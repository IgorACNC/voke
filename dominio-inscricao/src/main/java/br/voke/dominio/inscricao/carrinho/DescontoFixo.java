package br.voke.dominio.inscricao.carrinho;

import java.math.BigDecimal;
import java.util.Objects;

public class DescontoFixo implements EstrategiaDesconto {

    private final BigDecimal valor;

    public DescontoFixo(BigDecimal valor) {
        this.valor = Objects.requireNonNull(valor, "Valor do desconto é obrigatório");
    }

    @Override
    public BigDecimal calcular(BigDecimal subtotal) {
        return valor;
    }

    public BigDecimal getValor() {
        return valor;
    }
}
