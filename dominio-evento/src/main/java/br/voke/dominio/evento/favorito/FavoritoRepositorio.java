package br.voke.dominio.evento.favorito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FavoritoRepositorio {
    void salvar(Favorito favorito);
    Optional<Favorito> buscarPorId(FavoritoId id);
    List<Favorito> buscarPorParticipanteId(UUID participanteId);
    void remover(FavoritoId id);
    boolean existePorParticipanteEEvento(UUID participanteId, UUID eventoId);
}