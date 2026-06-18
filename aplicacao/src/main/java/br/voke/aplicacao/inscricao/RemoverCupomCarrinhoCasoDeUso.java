package br.voke.aplicacao.inscricao;

import br.voke.dominio.inscricao.carrinho.Carrinho;
import br.voke.dominio.inscricao.carrinho.CarrinhoServico;
import br.voke.dominio.inscricao.carrinho.CupomGateway;

import java.util.Objects;
import java.util.UUID;

public class RemoverCupomCarrinhoCasoDeUso {

    private final CarrinhoServico carrinhoServico;
    private final CupomGateway cupomGateway;

    public RemoverCupomCarrinhoCasoDeUso(CarrinhoServico carrinhoServico, CupomGateway cupomGateway) {
        Objects.requireNonNull(carrinhoServico);
        Objects.requireNonNull(cupomGateway);
        this.carrinhoServico = carrinhoServico;
        this.cupomGateway = cupomGateway;
    }

    public Carrinho executar(UUID participanteId, String cpfParticipante) {
        Carrinho carrinho = carrinhoServico.obterOuCriar(participanteId);
        String codigo = carrinho.getCupomAplicado();
        if (codigo != null && cpfParticipante != null) {
            cupomGateway.liberarUso(codigo, cpfParticipante);
        }
        return carrinhoServico.removerCupom(participanteId);
    }
}
