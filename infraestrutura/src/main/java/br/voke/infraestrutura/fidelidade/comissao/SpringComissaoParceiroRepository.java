package br.voke.infraestrutura.fidelidade.comissao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.voke.dominio.fidelidade.comissao.StatusComissao;

public interface SpringComissaoParceiroRepository extends JpaRepository<ComissaoParceiroJpa, UUID> {
    List<ComissaoParceiroJpa> findByParceiroId(UUID parceiroId);
    Optional<ComissaoParceiroJpa> findByInscricaoId(UUID inscricaoId);

    @Query("SELECT SUM(c.valor) FROM ComissaoParceiroJpa c WHERE c.parceiroId = :parceiroId AND c.status = :status")
    BigDecimal sumByParceiroIdAndStatus(@Param("parceiroId") UUID parceiroId, @Param("status") StatusComissao status);
}
