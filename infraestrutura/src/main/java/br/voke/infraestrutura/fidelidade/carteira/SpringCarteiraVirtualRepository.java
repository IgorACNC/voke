package br.voke.infraestrutura.fidelidade.carteira;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringCarteiraVirtualRepository extends JpaRepository<CarteiraVirtualJpa, UUID> {
    Optional<CarteiraVirtualJpa> findByParticipanteId(UUID participanteId);
}