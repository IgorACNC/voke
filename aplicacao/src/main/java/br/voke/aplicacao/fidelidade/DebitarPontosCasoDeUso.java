package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.pontos.ContaPontosServico;

import java.util.Objects;
import java.util.UUID;

public class DebitarPontosCasoDeUso {

    private final ContaPontosServico servico;

    public DebitarPontosCasoDeUso(ContaPontosServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID participanteId, int pontos) {
        servico.debitar(participanteId, pontos);
    }
}