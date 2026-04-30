package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.recompensa.RecompensaId;
import br.voke.dominio.fidelidade.recompensa.RecompensaServico;

import java.util.Objects;
import java.util.UUID;

public class EditarRecompensaCasoDeUso {

    private final RecompensaServico servico;

    public EditarRecompensaCasoDeUso(RecompensaServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executarAtualizarDescricao(UUID recompensaId, String novaDescricao) {
        servico.atualizarDescricao(new RecompensaId(recompensaId), novaDescricao);
    }

    public void executarAlterarCusto(UUID recompensaId, int novoCusto) {
        servico.alterarCusto(new RecompensaId(recompensaId), novoCusto);
    }
}
