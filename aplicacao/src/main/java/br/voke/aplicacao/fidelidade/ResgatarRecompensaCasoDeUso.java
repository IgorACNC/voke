package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.recompensa.LogRecompensaObserver;
import br.voke.dominio.fidelidade.recompensa.RecompensaId;
import br.voke.dominio.fidelidade.recompensa.RecompensaServico;

import java.util.Objects;
import java.util.UUID;

public class ResgatarRecompensaCasoDeUso {

    private final RecompensaServico servico;

    public ResgatarRecompensaCasoDeUso(RecompensaServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
        this.servico.adicionarObserver(new LogRecompensaObserver());
    }

    public void executar(UUID participanteId, UUID recompensaId) {
        servico.resgatar(participanteId, new RecompensaId(recompensaId));
    }
}
