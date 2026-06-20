package br.voke.infraestrutura.fidelidade.pontos;

import br.voke.dominio.fidelidade.pontos.TipoTransacaoPontos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringTransacaoPontosRepository extends JpaRepository<TransacaoPontosJpa, UUID> {
    List<TransacaoPontosJpa> findByParticipanteIdOrderByDataHoraDesc(UUID participanteId);

    @Query("select coalesce(sum(t.pontos), 0) from TransacaoPontosJpa t "
            + "where t.participanteId = :pid and t.tipo = :tipo and t.dataHora < :ate")
    Integer somarPontosPorTipoAteData(@Param("pid") UUID participanteId,
                                       @Param("tipo") TipoTransacaoPontos tipo,
                                       @Param("ate") LocalDateTime ate);

    @Query("select coalesce(sum(t.pontos), 0) from TransacaoPontosJpa t "
            + "where t.participanteId = :pid and t.tipo = :tipo")
    Integer somarPontosPorTipo(@Param("pid") UUID participanteId,
                                @Param("tipo") TipoTransacaoPontos tipo);
}
