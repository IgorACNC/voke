package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.pontos.ContaPontosServico;

import java.util.Objects;
import java.util.UUID;

public class CreditarPontosCasoDeUso {

    private final ContaPontosServico servico;

    public CreditarPontosCasoDeUso(ContaPontosServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID participanteId, int pontos,
                         boolean eventoEncerrado, boolean checkInRealizado) {
        servico.creditarPorPresenca(participanteId, pontos, eventoEncerrado, checkInRealizado);
    }
}