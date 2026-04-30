package br.voke.aplicacao.inscricao;

import br.voke.dominio.inscricao.carrinho.Carrinho;
import br.voke.dominio.inscricao.carrinho.CarrinhoServico;

import java.util.Objects;
import java.util.UUID;

public class RemoverDoCarrinhoCasoDeUso {

    private final CarrinhoServico servico;

    public RemoverDoCarrinhoCasoDeUso(CarrinhoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public Carrinho executar(UUID participanteId, UUID eventoId) {
        return servico.removerItem(participanteId, eventoId);
    }
}