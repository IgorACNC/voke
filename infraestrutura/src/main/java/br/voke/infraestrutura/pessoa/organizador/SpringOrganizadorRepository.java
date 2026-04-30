package br.voke.infraestrutura.pessoa.organizador;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringOrganizadorRepository extends JpaRepository<OrganizadorJpa, UUID> {
    Optional<OrganizadorJpa> findByEmail(String email);
    Optional<OrganizadorJpa> findByCpf(String cpf);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
}
