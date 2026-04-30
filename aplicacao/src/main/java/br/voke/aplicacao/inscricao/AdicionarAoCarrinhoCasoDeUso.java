package br.voke.aplicacao.inscricao;

import br.voke.dominio.inscricao.carrinho.Carrinho;
import br.voke.dominio.inscricao.carrinho.CarrinhoServico;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class AdicionarAoCarrinhoCasoDeUso {

    private final CarrinhoServico servico;

    public AdicionarAoCarrinhoCasoDeUso(CarrinhoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public Carrinho executar(UUID participanteId, UUID eventoId, String nomeEvento,
                             int quantidade, BigDecimal precoUnitario) {
        return servico.adicionarItem(participanteId, eventoId, nomeEvento, quantidade, precoUnitario);
    }
}