package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.recompensa.Recompensa;
import br.voke.dominio.fidelidade.recompensa.RecompensaServico;

import java.util.List;
import java.util.Objects;

public class ListarRecompensasAtivasCasoDeUso {

    private final RecompensaServico servico;

    public ListarRecompensasAtivasCasoDeUso(RecompensaServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public List<Recompensa> executar() {
        return servico.listarAtivas();
    }
}
