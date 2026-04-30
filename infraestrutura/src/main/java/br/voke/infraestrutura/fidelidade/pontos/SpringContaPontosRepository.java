package br.voke.infraestrutura.fidelidade.pontos;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringContaPontosRepository extends JpaRepository<ContaPontosJpa, UUID> {
    Optional<ContaPontosJpa> findByParticipanteId(UUID participanteId);
}