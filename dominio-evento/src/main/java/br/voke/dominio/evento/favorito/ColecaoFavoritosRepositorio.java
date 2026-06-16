package br.voke.dominio.evento.favorito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ColecaoFavoritosRepositorio {
    void salvar(ColecaoFavoritos colecao);
    Optional<ColecaoFavoritos> buscarPorId(ColecaoFavoritosId id);
    List<ColecaoFavoritos> buscarPorParticipanteId(UUID participanteId);
    void remover(ColecaoFavoritosId id);
    boolean existePorNomeEParticipante(String nome, UUID participanteId);
}
