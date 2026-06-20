package br.voke.aplicacao.evento;

import br.voke.dominio.evento.favorito.Favorito;
import br.voke.dominio.evento.favorito.FavoritoRepositorio;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListarFavoritosDoParticipanteCasoDeUso {

    private final FavoritoRepositorio repositorio;

    public ListarFavoritosDoParticipanteCasoDeUso(FavoritoRepositorio repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio);
    }

    public List<Favorito> executar(UUID participanteId) {
        return repositorio.buscarPorParticipanteId(participanteId);
    }
}
