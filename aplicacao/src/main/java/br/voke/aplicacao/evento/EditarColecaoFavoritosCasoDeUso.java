package br.voke.aplicacao.evento;

import br.voke.dominio.evento.favorito.ColecaoFavoritos;
import br.voke.dominio.evento.favorito.ColecaoFavoritosId;
import br.voke.dominio.evento.favorito.ColecaoFavoritosServico;
import br.voke.dominio.evento.favorito.VisibilidadeColecao;

import java.util.Objects;
import java.util.UUID;

public class EditarColecaoFavoritosCasoDeUso {

    private final ColecaoFavoritosServico servico;

    public EditarColecaoFavoritosCasoDeUso(ColecaoFavoritosServico servico) {
        this.servico = Objects.requireNonNull(servico);
    }

    public ColecaoFavoritos executar(UUID colecaoId, String novoNome, VisibilidadeColecao visibilidade) {
        return servico.editar(new ColecaoFavoritosId(colecaoId), novoNome, visibilidade);
    }
}
