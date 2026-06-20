package br.voke.dominio.fidelidade.carteira;

import java.math.BigDecimal;

public interface EstrategiaInsercaoSaldo {

    void validar(BigDecimal totalInseridoHoje, BigDecimal valor);
}
