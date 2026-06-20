package br.voke.infraestrutura.pessoa.participante;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringParticipanteRepository extends JpaRepository<ParticipanteJpa, UUID> {
    Optional<ParticipanteJpa> findByEmail(String email);
    Optional<ParticipanteJpa> findByCpf(String cpf);
    List<ParticipanteJpa> findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(String nome, String email, Pageable pageable);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
}
