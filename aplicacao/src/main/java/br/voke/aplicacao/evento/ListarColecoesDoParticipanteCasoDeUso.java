package br.voke.aplicacao.evento;

import br.voke.dominio.evento.favorito.ColecaoFavoritos;
import br.voke.dominio.evento.favorito.ColecaoFavoritosServico;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListarColecoesDoParticipanteCasoDeUso {

    private final ColecaoFavoritosServico servico;

    public ListarColecoesDoParticipanteCasoDeUso(ColecaoFavoritosServico servico) {
        this.servico = Objects.requireNonNull(servico);
    }

    public List<ColecaoFavoritos> executar(UUID participanteId) {
        return servico.listarDoParticipante(participanteId);
    }
}
