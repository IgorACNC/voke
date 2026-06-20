package br.voke.dominio.fidelidade.carteira;

import br.voke.dominio.fidelidade.excecao.LimiteDiarioInsercaoException;

import java.math.BigDecimal;

public class InsercaoSaldoVip implements EstrategiaInsercaoSaldo {

    private static final BigDecimal LIMITE_DIARIO = new BigDecimal("10000.00");

    @Override
    public void validar(BigDecimal totalInseridoHoje, BigDecimal valor) {
        if (totalInseridoHoje.add(valor).compareTo(LIMITE_DIARIO) > 0) {
            throw new LimiteDiarioInsercaoException();
        }
    }
}
