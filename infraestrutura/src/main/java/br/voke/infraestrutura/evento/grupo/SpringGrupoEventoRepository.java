package br.voke.infraestrutura.evento.grupo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringGrupoEventoRepository extends JpaRepository<GrupoEventoJpa, UUID> {
    Optional<GrupoEventoJpa> findByEventoId(UUID eventoId);
}
