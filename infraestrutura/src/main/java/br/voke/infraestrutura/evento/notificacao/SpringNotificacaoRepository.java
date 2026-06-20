package br.voke.infraestrutura.evento.notificacao;

import br.voke.dominio.evento.notificacao.StatusNotificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringNotificacaoRepository extends JpaRepository<NotificacaoJpa, UUID> {
    List<NotificacaoJpa> findByEventoId(UUID eventoId);

    @Query("SELECT n FROM NotificacaoJpa n WHERE n.eventoId IN" +
           " (SELECT i.eventoId FROM InscricaoJpa i WHERE i.participanteId = :participanteId)")
    List<NotificacaoJpa> findByParticipanteId(@Param("participanteId") UUID participanteId);

    List<NotificacaoJpa> findByStatusAndDataAgendamentoLessThanEqual(
            StatusNotificacao status, LocalDateTime dataHora);
}
