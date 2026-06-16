package br.voke.infraestrutura.evento.favorito;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringColecaoFavoritosRepository extends JpaRepository<ColecaoFavoritosJpa, UUID> {
    List<ColecaoFavoritosJpa> findByParticipanteId(UUID participanteId);
    boolean existsByNomeAndParticipanteId(String nome, UUID participanteId);
}
