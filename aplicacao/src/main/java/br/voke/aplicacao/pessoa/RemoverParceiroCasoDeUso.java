package br.voke.aplicacao.pessoa;

import br.voke.dominio.pessoa.parceiro.ParceiroId;
import br.voke.dominio.pessoa.parceiro.ParceiroServico;

import java.util.Objects;
import java.util.UUID;

public class RemoverParceiroCasoDeUso {

    private final ParceiroServico servico;

    public RemoverParceiroCasoDeUso(ParceiroServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID parceiroId) {
        servico.remover(new ParceiroId(parceiroId));
    }
}