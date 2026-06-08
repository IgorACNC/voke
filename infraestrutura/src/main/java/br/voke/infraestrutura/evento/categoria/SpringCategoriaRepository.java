package br.voke.infraestrutura.evento.categoria;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringCategoriaRepository extends JpaRepository<CategoriaJpa, UUID> {
    Optional<CategoriaJpa> findByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, UUID id);
}
