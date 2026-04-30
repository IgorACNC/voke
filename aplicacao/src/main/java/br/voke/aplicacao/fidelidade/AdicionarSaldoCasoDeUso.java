package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.carteira.CarteiraVirtualServico;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class AdicionarSaldoCasoDeUso {

    private final CarteiraVirtualServico servico;

    public AdicionarSaldoCasoDeUso(CarteiraVirtualServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID participanteId, BigDecimal valor) {
        servico.adicionarSaldo(participanteId, valor);
    }
}