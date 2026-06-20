package br.voke.dominio.inscricao.carrinho;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TaxaCartaoCredito implements EstrategiaTaxa {

    private static final BigDecimal TAXA = new BigDecimal("0.05");

    @Override
    public BigDecimal aplicar(BigDecimal totalComDesconto) {
        BigDecimal taxa = totalComDesconto.multiply(TAXA).setScale(2, RoundingMode.HALF_UP);
        return totalComDesconto.add(taxa);
    }
}
