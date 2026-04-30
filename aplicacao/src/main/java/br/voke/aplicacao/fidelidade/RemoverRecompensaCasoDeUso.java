package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.recompensa.RecompensaId;
import br.voke.dominio.fidelidade.recompensa.RecompensaServico;

import java.util.Objects;
import java.util.UUID;

public class RemoverRecompensaCasoDeUso {

    private final RecompensaServico servico;

    public RemoverRecompensaCasoDeUso(RecompensaServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID recompensaId) {
        servico.remover(new RecompensaId(recompensaId));
    }
}
