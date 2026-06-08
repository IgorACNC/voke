package br.voke.infraestrutura.evento.evento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface SpringEventoRepository extends JpaRepository<EventoJpa, UUID> {
    Optional<EventoJpa> findByNome(String nome);
    boolean existsByNome(String nome);
    List<EventoJpa> findByLocalIgnoreCase(String local);

    @org.springframework.data.jpa.repository.Query(
            "SELECT DISTINCT e FROM EventoJpa e JOIN e.tags t WHERE LOWER(t) IN :tags")
    List<EventoJpa> findByTagsIn(@org.springframework.data.repository.query.Param("tags") Set<String> tags);
}
