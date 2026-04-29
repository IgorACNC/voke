package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.sugestao.SugestaoId;
import br.voke.dominio.fidelidade.sugestao.SugestaoServico;

import java.util.Objects;
import java.util.UUID;

public class ExpirarSugestaoCasoDeUso {

    private final SugestaoServico servico;

    public ExpirarSugestaoCasoDeUso(SugestaoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID sugestaoId) {
        servico.expirar(new SugestaoId(sugestaoId));
    }
}
