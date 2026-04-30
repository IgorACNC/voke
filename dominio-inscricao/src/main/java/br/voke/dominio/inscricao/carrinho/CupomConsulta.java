package br.voke.dominio.inscricao.carrinho;

import java.math.BigDecimal;

public interface CupomConsulta {
    BigDecimal validar(String codigoCupom);
}