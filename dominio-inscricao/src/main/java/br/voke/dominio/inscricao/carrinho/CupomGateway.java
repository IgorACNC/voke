package br.voke.dominio.inscricao.carrinho;

import java.math.BigDecimal;

public interface CupomGateway {
    BigDecimal validarEUtilizar(String codigoCupom, String cpf);
}
