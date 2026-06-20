package br.voke.aplicacao.evento;

import br.voke.dominio.evento.favorito.ColecaoFavoritos;
import br.voke.dominio.evento.favorito.ColecaoFavoritosId;
import br.voke.dominio.evento.favorito.ColecaoFavoritosServico;

import java.util.Objects;
import java.util.UUID;

public class BuscarColecaoCasoDeUso {

    private final ColecaoFavoritosServico servico;

    public BuscarColecaoCasoDeUso(ColecaoFavoritosServico servico) {
        this.servico = Objects.requireNonNull(servico);
    }

    public ColecaoFavoritos executar(UUID colecaoId) {
        return servico.buscarPorId(new ColecaoFavoritosId(colecaoId));
    }
}
