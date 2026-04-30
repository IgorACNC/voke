package br.voke.infraestrutura.pessoa.participante;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringParticipanteRepository extends JpaRepository<ParticipanteJpa, UUID> {
    Optional<ParticipanteJpa> findByEmail(String email);
    Optional<ParticipanteJpa> findByCpf(String cpf);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
}
