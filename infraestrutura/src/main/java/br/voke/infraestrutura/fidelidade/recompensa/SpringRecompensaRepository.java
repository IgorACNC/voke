package br.voke.infraestrutura.fidelidade.recompensa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringRecompensaRepository extends JpaRepository<RecompensaJpa, UUID> {
    List<RecompensaJpa> findByOrganizadorId(UUID organizadorId);
}
