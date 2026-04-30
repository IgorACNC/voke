package br.voke.aplicacao.pessoa;

import br.voke.dominio.pessoa.amizade.AmizadeId;
import br.voke.dominio.pessoa.amizade.AmizadeServico;

import java.util.Objects;
import java.util.UUID;

public class RecusarAmizadeCasoDeUso {

    private final AmizadeServico servico;

    public RecusarAmizadeCasoDeUso(AmizadeServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID amizadeId) {
        servico.recusar(new AmizadeId(amizadeId));
    }
}
