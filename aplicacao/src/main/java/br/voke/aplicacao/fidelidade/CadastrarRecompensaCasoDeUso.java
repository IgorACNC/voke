package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.recompensa.Recompensa;
import br.voke.dominio.fidelidade.recompensa.RecompensaServico;

import java.util.Objects;
import java.util.UUID;

public class CadastrarRecompensaCasoDeUso {

    private final RecompensaServico servico;

    public CadastrarRecompensaCasoDeUso(RecompensaServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public Recompensa executar(String nome, String descricao, int custoEmPontos,
                               int estoqueTotal, UUID organizadorId) {
        return servico.cadastrar(nome, descricao, custoEmPontos, estoqueTotal, organizadorId);
    }
}
