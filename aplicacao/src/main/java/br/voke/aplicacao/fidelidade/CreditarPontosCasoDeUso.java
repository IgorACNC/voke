package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.pontos.ContaPontosServico;
import br.voke.dominio.fidelidade.pontos.EstrategiaGanhoPontos;

import java.util.Objects;
import java.util.UUID;

public class CreditarPontosCasoDeUso {

    private final ContaPontosServico servico;

    public CreditarPontosCasoDeUso(ContaPontosServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID participanteId, int pontosBase,
                         boolean eventoEncerrado, boolean checkInRealizado,
                         EstrategiaGanhoPontos estrategia) {
        servico.creditarPorPresenca(participanteId, pontosBase, eventoEncerrado,
                checkInRealizado, estrategia);
    }
}