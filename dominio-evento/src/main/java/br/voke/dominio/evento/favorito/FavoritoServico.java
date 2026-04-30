package br.voke.dominio.evento.favorito;

import br.voke.dominio.evento.excecao.FavoritoDuplicadoException;
import br.voke.dominio.evento.excecao.StatusEventoInvalidoException;

import java.util.Objects;
import java.util.UUID;

public class FavoritoServico {

    private final FavoritoRepositorio repositorio;

    public FavoritoServico(FavoritoRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
    }

    public Favorito adicionar(UUID participanteId, UUID eventoId, String statusEvento) {
        if (!statusEvento.equals("PUBLICADO") && !statusEvento.equals("ATIVO")) {
            throw new StatusEventoInvalidoException("Este evento não pode ser adicionado aos favoritos");
        }
        if (repositorio.existePorParticipanteEEvento(participanteId, eventoId)) {
            throw new FavoritoDuplicadoException();
        }
        Favorito favorito = new Favorito(FavoritoId.novo(), participanteId, eventoId);
        repositorio.salvar(favorito);
        return favorito;
    }

    public void remover(FavoritoId id) {
        repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Favorito não encontrado"));
        repositorio.remover(id);
    }
}