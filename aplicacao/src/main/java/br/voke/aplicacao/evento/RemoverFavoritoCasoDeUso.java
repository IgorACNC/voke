package br.voke.aplicacao.evento;

import br.voke.dominio.evento.favorito.FavoritoId;
import br.voke.dominio.evento.favorito.FavoritoServico;

import java.util.Objects;
import java.util.UUID;

public class RemoverFavoritoCasoDeUso {

    private final FavoritoServico servico;

    public RemoverFavoritoCasoDeUso(FavoritoServico servico) {
        this.servico = Objects.requireNonNull(servico);
    }

    public void executar(UUID favoritoId) {
        servico.remover(new FavoritoId(favoritoId));
    }
}
