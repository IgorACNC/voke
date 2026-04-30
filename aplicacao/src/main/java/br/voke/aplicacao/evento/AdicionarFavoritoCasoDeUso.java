package br.voke.aplicacao.evento;

import br.voke.dominio.evento.favorito.Favorito;
import br.voke.dominio.evento.favorito.FavoritoServico;

import java.util.Objects;
import java.util.UUID;

public class AdicionarFavoritoCasoDeUso {

    private final FavoritoServico servico;

    public AdicionarFavoritoCasoDeUso(FavoritoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public Favorito executar(UUID participanteId, UUID eventoId, String statusEvento) {
        return servico.adicionar(participanteId, eventoId, statusEvento);
    }
}