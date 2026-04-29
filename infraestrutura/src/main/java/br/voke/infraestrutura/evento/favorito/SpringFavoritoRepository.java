package br.voke.infraestrutura.evento.favorito;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringFavoritoRepository extends JpaRepository<FavoritoJpa, UUID> {
    List<FavoritoJpa> findByParticipanteId(UUID participanteId);
    boolean existsByParticipanteIdAndEventoId(UUID participanteId, UUID eventoId);
}