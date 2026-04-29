package br.voke.infraestrutura.evento.notificacao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringNotificacaoRepository extends JpaRepository<NotificacaoJpa, UUID> {
    List<NotificacaoJpa> findByEventoId(UUID eventoId);
}
