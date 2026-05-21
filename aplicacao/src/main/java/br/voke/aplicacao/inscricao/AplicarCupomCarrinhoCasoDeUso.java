package br.voke.aplicacao.inscricao;

import br.voke.dominio.inscricao.carrinho.Carrinho;
import br.voke.dominio.inscricao.carrinho.CarrinhoServico;
import br.voke.dominio.inscricao.carrinho.CupomGateway;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class AplicarCupomCarrinhoCasoDeUso {

    private final CarrinhoServico carrinhoServico;
    private final CupomGateway cupomGateway;

    public AplicarCupomCarrinhoCasoDeUso(CarrinhoServico carrinhoServico,
                                         CupomGateway cupomGateway) {
        Objects.requireNonNull(carrinhoServico);
        Objects.requireNonNull(cupomGateway);
        this.carrinhoServico = carrinhoServico;
        this.cupomGateway = cupomGateway;
    }

    public Carrinho executar(UUID participanteId, String codigoCupom, String cpfParticipante) {
        BigDecimal desconto = cupomGateway.validarEUtilizar(codigoCupom, cpfParticipante);
        return carrinhoServico.aplicarCupom(participanteId, codigoCupom, desconto);
    }
}