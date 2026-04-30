package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.recompensa.Recompensa;
import br.voke.dominio.fidelidade.recompensa.RecompensaServico;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListarRecompensasOrganizadorCasoDeUso {

    private final RecompensaServico servico;

    public ListarRecompensasOrganizadorCasoDeUso(RecompensaServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public List<Recompensa> executar(UUID organizadorId) {
        return servico.listarPorOrganizador(organizadorId);
    }
}
