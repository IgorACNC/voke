package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.pontos.ContaPontosServico;

import java.util.Objects;
import java.util.UUID;

public class ConsultarSaldoPontosCasoDeUso {

    private final ContaPontosServico servico;

    public ConsultarSaldoPontosCasoDeUso(ContaPontosServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public int executar(UUID participanteId) {
        return servico.consultarSaldo(participanteId);
    }
}