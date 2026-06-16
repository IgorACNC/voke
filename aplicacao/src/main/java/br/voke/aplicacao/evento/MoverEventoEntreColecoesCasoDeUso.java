package br.voke.aplicacao.evento;

import br.voke.dominio.evento.favorito.ColecaoFavoritos;
import br.voke.dominio.evento.favorito.ColecaoFavoritosId;
import br.voke.dominio.evento.favorito.ColecaoFavoritosServico;

import java.util.Objects;
import java.util.UUID;

public class MoverEventoEntreColecoesCasoDeUso {

    private final ColecaoFavoritosServico servico;

    public MoverEventoEntreColecoesCasoDeUso(ColecaoFavoritosServico servico) {
        this.servico = Objects.requireNonNull(servico);
    }

    public ColecaoFavoritos executar(UUID origemId, UUID destinoId, UUID eventoId) {
        return servico.moverEvento(
                new ColecaoFavoritosId(origemId),
                new ColecaoFavoritosId(destinoId),
                eventoId);
    }
}
