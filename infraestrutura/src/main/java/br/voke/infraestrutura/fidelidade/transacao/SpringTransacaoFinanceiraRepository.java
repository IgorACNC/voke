package br.voke.infraestrutura.fidelidade.transacao;

import br.voke.dominio.fidelidade.transacao.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringTransacaoFinanceiraRepository extends JpaRepository<TransacaoFinanceiraJpa, UUID> {
    List<TransacaoFinanceiraJpa> findByParticipanteIdOrderByDataHoraDesc(UUID participanteId);

    @Query("select coalesce(sum(t.valor), 0) from TransacaoFinanceiraJpa t "
            + "where t.participanteId = :pid and t.tipo = :tipo and t.dataHora >= :desde")
    BigDecimal somarPorTipoDesde(@Param("pid") UUID participanteId,
                                  @Param("tipo") TipoTransacao tipo,
                                  @Param("desde") LocalDateTime desde);

    long countByParticipanteIdAndTipoAndDataHoraGreaterThanEqual(UUID participanteId,
                                                                   TipoTransacao tipo,
                                                                   LocalDateTime desde);
}
