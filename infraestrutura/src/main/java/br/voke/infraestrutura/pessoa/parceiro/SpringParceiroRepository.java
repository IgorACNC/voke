package br.voke.infraestrutura.pessoa.parceiro;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringParceiroRepository extends JpaRepository<ParceiroJpa, UUID> {
    List<ParceiroJpa> findByOrganizadorId(UUID organizadorId);
    List<ParceiroJpa> findByParticipanteId(UUID participanteId);
    Optional<ParceiroJpa> findByParticipanteIdAndOrganizadorId(UUID participanteId, UUID organizadorId);
}
