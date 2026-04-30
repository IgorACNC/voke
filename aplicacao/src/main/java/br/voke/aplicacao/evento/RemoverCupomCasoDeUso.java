package br.voke.aplicacao.evento;

import br.voke.dominio.evento.cupom.CupomId;
import br.voke.dominio.evento.cupom.CupomServico;

import java.util.Objects;
import java.util.UUID;

public class RemoverCupomCasoDeUso {

    private final CupomServico servico;

    public RemoverCupomCasoDeUso(CupomServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID cupomId) {
        servico.remover(new CupomId(cupomId));
    }
}
