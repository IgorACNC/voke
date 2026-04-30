package br.voke.aplicacao.pessoa;

import br.voke.dominio.pessoa.parceiro.AtividadeParceiro;
import br.voke.dominio.pessoa.parceiro.ParceiroId;
import br.voke.dominio.pessoa.parceiro.ParceiroServico;

import java.util.Objects;
import java.util.UUID;

public class EditarParceiroCasoDeUso {

    private final ParceiroServico servico;

    public EditarParceiroCasoDeUso(ParceiroServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void adicionarAtividade(UUID parceiroId, AtividadeParceiro atividade) {
        servico.adicionarAtividade(new ParceiroId(parceiroId), atividade);
    }

    public void removerAtividade(UUID parceiroId, AtividadeParceiro atividade) {
        servico.removerAtividade(new ParceiroId(parceiroId), atividade);
    }
}