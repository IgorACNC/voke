package br.voke.aplicacao.evento;

import br.voke.dominio.evento.favorito.ColecaoFavoritos;
import br.voke.dominio.evento.favorito.ColecaoFavoritosId;
import br.voke.dominio.evento.favorito.ColecaoFavoritosServico;

import java.util.Objects;
import java.util.UUID;

public class AdicionarEventoColecaoCasoDeUso {

    private final ColecaoFavoritosServico servico;

    public AdicionarEventoColecaoCasoDeUso(ColecaoFavoritosServico servico) {
        this.servico = Objects.requireNonNull(servico);
    }

    public ColecaoFavoritos executar(UUID colecaoId, UUID eventoId) {
        return servico.adicionarEvento(new ColecaoFavoritosId(colecaoId), eventoId);
    }
}
