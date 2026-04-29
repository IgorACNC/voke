package br.voke.aplicacao.pessoa;

import br.voke.dominio.pessoa.amizade.AmizadeId;
import br.voke.dominio.pessoa.amizade.AmizadeServico;

import java.util.Objects;
import java.util.UUID;

public class DesfazerAmizadeCasoDeUso {

    private final AmizadeServico servico;

    public DesfazerAmizadeCasoDeUso(AmizadeServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID amizadeId) {
        servico.desfazer(new AmizadeId(amizadeId));
    }
}
