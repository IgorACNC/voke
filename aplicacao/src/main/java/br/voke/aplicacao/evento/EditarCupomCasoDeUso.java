package br.voke.aplicacao.evento;

import br.voke.dominio.evento.cupom.CupomId;
import br.voke.dominio.evento.cupom.CupomServico;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class EditarCupomCasoDeUso {

    private final CupomServico servico;

    public EditarCupomCasoDeUso(CupomServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID cupomId, BigDecimal novoDesconto, int novaQuantidadeMaxima) {
        servico.editar(new CupomId(cupomId), novoDesconto, novaQuantidadeMaxima);
    }
}
