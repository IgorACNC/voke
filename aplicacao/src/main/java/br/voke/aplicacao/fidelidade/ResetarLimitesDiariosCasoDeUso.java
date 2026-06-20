package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.carteira.CarteiraVirtualServico;

import java.util.Objects;

public class ResetarLimitesDiariosCasoDeUso {

    private final CarteiraVirtualServico servico;

    public ResetarLimitesDiariosCasoDeUso(CarteiraVirtualServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar() {
        servico.resetarLimitesDiarios();
    }
}
