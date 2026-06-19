package br.voke.infraestrutura.evento.avaliacao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringAvaliacaoRepository extends JpaRepository<AvaliacaoJpa, UUID> {
    Optional<AvaliacaoJpa> findByParticipanteIdAndEventoId(UUID participanteId, UUID eventoId);
    boolean existsByParticipanteIdAndEventoId(UUID participanteId, UUID eventoId);
    List<AvaliacaoJpa> findByEventoId(UUID eventoId);
}
