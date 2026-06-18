package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.comissao.ComissaoParceiro;
import br.voke.dominio.fidelidade.comissao.ComissaoParceiroServico;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ConsultarComissoesCasoDeUso {

    private final ComissaoParceiroServico servico;

    public ConsultarComissoesCasoDeUso(ComissaoParceiroServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public List<ComissaoParceiro> listar(UUID parceiroId) {
        return servico.listarComissoes(parceiroId);
    }

    public BigDecimal consultarSaldo(UUID parceiroId) {
        return servico.consultarSaldo(parceiroId);
    }
}
