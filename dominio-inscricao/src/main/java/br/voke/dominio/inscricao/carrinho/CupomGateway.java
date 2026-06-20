package br.voke.dominio.inscricao.carrinho;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public interface CupomGateway {
    BigDecimal validarEUtilizar(String codigoCupom, String cpf,
                                Map<UUID, UUID> organizadorPorEvento, BigDecimal subtotal);
    void liberarUso(String codigoCupom, String cpf);
}
