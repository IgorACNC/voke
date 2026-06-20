package br.voke.infraestrutura.fidelidade.recompensa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringCupomResgatadoRepository extends JpaRepository<CupomResgatadoJpa, UUID> {
    List<CupomResgatadoJpa> findByParticipanteIdOrderByDataResgateDesc(UUID participanteId);
}
