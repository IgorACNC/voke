package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.sugestao.Sugestao;
import br.voke.dominio.fidelidade.sugestao.SugestaoServico;

import java.util.Objects;
import java.util.UUID;

public class CadastrarSugestaoCasoDeUso {

    private final SugestaoServico servico;

    public CadastrarSugestaoCasoDeUso(SugestaoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public Sugestao executar(UUID participanteId, UUID eventoId, String descricao) {
        return servico.cadastrar(participanteId, eventoId, descricao);
    }
}
