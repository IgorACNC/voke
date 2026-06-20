package br.voke.aplicacao.evento;

import br.voke.dominio.evento.favorito.ColecaoFavoritosId;
import br.voke.dominio.evento.favorito.ColecaoFavoritosServico;

import java.util.Objects;
import java.util.UUID;

public class ExcluirColecaoFavoritosCasoDeUso {

    private final ColecaoFavoritosServico servico;

    public ExcluirColecaoFavoritosCasoDeUso(ColecaoFavoritosServico servico) {
        this.servico = Objects.requireNonNull(servico);
    }

    public void executar(UUID colecaoId) {
        servico.excluir(new ColecaoFavoritosId(colecaoId));
    }
}
