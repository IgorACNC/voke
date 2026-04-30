package br.voke.infraestrutura.evento.cupom;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringCupomRepository extends JpaRepository<CupomJpa, UUID> {
    Optional<CupomJpa> findByCodigo(String codigo);
}
