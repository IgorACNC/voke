package br.voke.aplicacao.pessoa;

import br.voke.dominio.pessoa.amizade.AmizadeId;
import br.voke.dominio.pessoa.amizade.AmizadeServico;

import java.util.Objects;
import java.util.UUID;

public class AceitarAmizadeCasoDeUso {

    private final AmizadeServico servico;

    public AceitarAmizadeCasoDeUso(AmizadeServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID amizadeId) {
        servico.aceitar(new AmizadeId(amizadeId));
    }
}
