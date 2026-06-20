package br.voke.dominio.inscricao.carrinho;

import java.math.BigDecimal;

public interface EstrategiaTaxa {
    BigDecimal aplicar(BigDecimal totalComDesconto);
}
