package br.voke.aplicacao.evento;

import br.voke.dominio.evento.favorito.ColecaoFavoritos;
import br.voke.dominio.evento.favorito.ColecaoFavoritosId;
import br.voke.dominio.evento.favorito.ColecaoFavoritosServico;

import java.util.Objects;
import java.util.UUID;

public class DuplicarColecaoCasoDeUso {

    private final ColecaoFavoritosServico servico;

    public DuplicarColecaoCasoDeUso(ColecaoFavoritosServico servico) {
        this.servico = Objects.requireNonNull(servico);
    }

    public ColecaoFavoritos executar(UUID colecaoId, UUID participanteId) {
        return servico.duplicar(new ColecaoFavoritosId(colecaoId), participanteId);
    }
}
