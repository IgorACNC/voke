package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.sugestao.SugestaoId;
import br.voke.dominio.fidelidade.sugestao.SugestaoServico;

import java.util.Objects;
import java.util.UUID;

public class RemoverSugestaoCasoDeUso {

    private final SugestaoServico servico;

    public RemoverSugestaoCasoDeUso(SugestaoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID sugestaoId) {
        servico.remover(new SugestaoId(sugestaoId));
    }
}
