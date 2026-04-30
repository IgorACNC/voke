package br.voke.infraestrutura.evento.evento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringEventoRepository extends JpaRepository<EventoJpa, UUID> {
    Optional<EventoJpa> findByNome(String nome);
    boolean existsByNome(String nome);
    List<EventoJpa> findByLocalIgnoreCase(String local);
}
