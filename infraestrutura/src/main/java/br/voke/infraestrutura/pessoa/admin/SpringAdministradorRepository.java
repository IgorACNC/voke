package br.voke.infraestrutura.pessoa.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringAdministradorRepository extends JpaRepository<AdministradorJpa, UUID> {
    Optional<AdministradorJpa> findByEmail(String email);
    boolean existsByEmail(String email);
}
