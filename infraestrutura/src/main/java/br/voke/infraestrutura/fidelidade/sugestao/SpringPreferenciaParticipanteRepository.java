package br.voke.infraestrutura.fidelidade.sugestao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringPreferenciaParticipanteRepository extends JpaRepository<PreferenciaParticipanteJpa, UUID> {
    Optional<PreferenciaParticipanteJpa> findByParticipanteId(UUID participanteId);
}
