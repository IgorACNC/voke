package br.voke.infraestrutura.evento.estatistica;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringEstatisticaEventoRepository extends JpaRepository<EstatisticaEventoJpa, UUID> {
    Optional<EstatisticaEventoJpa> findByEventoId(UUID eventoId);
    List<EstatisticaEventoJpa> findByOrganizadorId(UUID organizadorId);
}
