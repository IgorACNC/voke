package br.voke.dominio.inscricao.carrinho;

import java.math.BigDecimal;

public interface EstrategiaDesconto {
    BigDecimal calcular(BigDecimal subtotal);
}
