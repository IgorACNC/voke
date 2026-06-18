package br.voke.dominio.fidelidade.comissao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComissaoParceiroRepositorio {
    void salvar(ComissaoParceiro comissao);
    List<ComissaoParceiro> buscarPorParceiroId(UUID parceiroId);
    Optional<ComissaoParceiro> buscarPorInscricaoId(UUID inscricaoId);
    BigDecimal calcularSaldoParceiro(UUID parceiroId);
}
