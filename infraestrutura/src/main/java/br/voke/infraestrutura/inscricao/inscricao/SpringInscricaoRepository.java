package br.voke.infraestrutura.inscricao.inscricao;

import br.voke.dominio.inscricao.inscricao.StatusInscricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringInscricaoRepository extends JpaRepository<InscricaoJpa, UUID> {
    List<InscricaoJpa> findByParticipanteId(UUID participanteId);
    List<InscricaoJpa> findByEventoId(UUID eventoId);
    int countByParticipanteIdAndEventoId(UUID participanteId, UUID eventoId);

    @Query("SELECT COUNT(i) FROM InscricaoJpa i, EventoJpa e WHERE i.eventoId = e.id" +
           " AND i.participanteId = :participanteId AND e.organizadorId = :organizadorId" +
           " AND i.status = :status")
    int countEventosConfirmadosPorOrganizador(@Param("participanteId") UUID participanteId,
                                              @Param("organizadorId") UUID organizadorId,
                                              @Param("status") StatusInscricao status);

    @Query("SELECT COUNT(i) FROM InscricaoJpa i, EventoJpa e WHERE i.eventoId = e.id" +
           " AND i.participanteId = :participanteId AND i.status <> :cancelada" +
           " AND e.dataHoraInicio < :fim AND e.dataHoraFim > :inicio")
    long countConflitoDeHorario(@Param("participanteId") UUID participanteId,
                                @Param("inicio") LocalDateTime inicio,
                                @Param("fim") LocalDateTime fim,
                                @Param("cancelada") StatusInscricao cancelada);

    @Query("SELECT CAST(i.dataInscricao AS date) AS d, COUNT(i), SUM(i.valorPago) " +
           "FROM InscricaoJpa i " +
           "WHERE i.eventoId = :eventoId AND i.status <> :cancelada " +
           "GROUP BY CAST(i.dataInscricao AS date) ORDER BY CAST(i.dataInscricao AS date)")
    List<Object[]> curvaVendas(@Param("eventoId") UUID eventoId,
                               @Param("cancelada") StatusInscricao cancelada);
}
