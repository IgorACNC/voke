package br.voke.aplicacao.evento;

import br.voke.dominio.evento.favorito.ColecaoFavoritos;
import br.voke.dominio.evento.favorito.ColecaoFavoritosId;
import br.voke.dominio.evento.favorito.ColecaoFavoritosServico;

import java.util.Objects;
import java.util.UUID;

public class ReordenarEventoColecaoCasoDeUso {

    private final ColecaoFavoritosServico servico;

    public ReordenarEventoColecaoCasoDeUso(ColecaoFavoritosServico servico) {
        this.servico = Objects.requireNonNull(servico);
    }

    public ColecaoFavoritos executar(UUID colecaoId, UUID eventoId, int novaOrdem) {
        return servico.reordenar(new ColecaoFavoritosId(colecaoId), eventoId, novaOrdem);
    }
}
