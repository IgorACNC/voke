package br.voke.aplicacao.evento;

import br.voke.dominio.evento.favorito.ColecaoFavoritos;
import br.voke.dominio.evento.favorito.ColecaoFavoritosServico;
import br.voke.dominio.evento.favorito.VisibilidadeColecao;

import java.util.Objects;
import java.util.UUID;

public class CriarColecaoFavoritosCasoDeUso {

    private final ColecaoFavoritosServico servico;

    public CriarColecaoFavoritosCasoDeUso(ColecaoFavoritosServico servico) {
        this.servico = Objects.requireNonNull(servico);
    }

    public ColecaoFavoritos executar(UUID participanteId, String nome, VisibilidadeColecao visibilidade) {
        return servico.criar(participanteId, nome, visibilidade);
    }
}
