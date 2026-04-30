package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.sugestao.SugestaoId;
import br.voke.dominio.fidelidade.sugestao.SugestaoServico;

import java.util.Objects;
import java.util.UUID;

public class EditarSugestaoCasoDeUso {

    private final SugestaoServico servico;

    public EditarSugestaoCasoDeUso(SugestaoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID sugestaoId, String novaDescricao) {
        servico.editar(new SugestaoId(sugestaoId), novaDescricao);
    }
}
