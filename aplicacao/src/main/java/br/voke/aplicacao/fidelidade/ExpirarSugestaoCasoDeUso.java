package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.pontos.ContaPontosServico;

import java.util.Objects;
import java.util.UUID;

public class ExpirarPontosCasoDeUso {

    private final ContaPontosServico servico;

    public ExpirarPontosCasoDeUso(ContaPontosServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID participanteId, int pontosExpirados) {
        servico.expirarPontos(participanteId, pontosExpirados);
    }
}
