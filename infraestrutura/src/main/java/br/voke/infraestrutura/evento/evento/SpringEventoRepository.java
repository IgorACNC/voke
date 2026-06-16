package br.voke.infraestrutura.evento.evento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface SpringEventoRepository extends JpaRepository<EventoJpa, UUID> {
    Optional<EventoJpa> findByNome(String nome);
    boolean existsByNome(String nome);
    List<EventoJpa> findByLocalIgnoreCase(String local);
    List<EventoJpa> findByOrganizadorId(UUID organizadorId);
    List<EventoJpa> findByStatus(br.voke.dominio.evento.evento.StatusEvento status);
    List<EventoJpa> findByStatusAndDataHoraFimBefore(
            br.voke.dominio.evento.evento.StatusEvento status, LocalDateTime dataHoraFim);

    @org.springframework.data.jpa.repository.Query(
            "SELECT DISTINCT e FROM EventoJpa e JOIN e.categoriaIds c WHERE c IN :categoriaIds")
    List<EventoJpa> findByCategoriaIdsIn(@org.springframework.data.repository.query.Param("categoriaIds") Set<UUID> categoriaIds);
}
