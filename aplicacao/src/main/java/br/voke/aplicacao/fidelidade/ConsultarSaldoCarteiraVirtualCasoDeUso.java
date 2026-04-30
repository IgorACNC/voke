package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.carteira.CarteiraVirtualServico;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class ConsultarSaldoCarteiraVirtualCasoDeUso {

    private final CarteiraVirtualServico servico;

    public ConsultarSaldoCarteiraVirtualCasoDeUso(CarteiraVirtualServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public BigDecimal executar(UUID participanteId) {
        return servico.consultarSaldo(participanteId);
    }
}