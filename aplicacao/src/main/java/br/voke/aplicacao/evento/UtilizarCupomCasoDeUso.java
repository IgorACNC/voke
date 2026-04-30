package br.voke.aplicacao.evento;

import br.voke.dominio.evento.cupom.CupomServico;

import java.math.BigDecimal;
import java.util.Objects;

public class UtilizarCupomCasoDeUso {

    private final CupomServico servico;

    public UtilizarCupomCasoDeUso(CupomServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public BigDecimal executar(String codigoCupom, String cpfParticipante) {
        return servico.validarEUtilizar(codigoCupom, cpfParticipante);
    }
}
