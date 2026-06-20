package br.voke.dominio.inscricao.carrinho;

import java.math.BigDecimal;

public class SemTaxa implements EstrategiaTaxa {

    @Override
    public BigDecimal aplicar(BigDecimal totalComDesconto) {
        return totalComDesconto;
    }
}
