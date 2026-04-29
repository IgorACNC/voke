package br.voke.aplicacao.inscricao;

import br.voke.dominio.evento.cupom.CupomServico;
import br.voke.dominio.inscricao.carrinho.Carrinho;
import br.voke.dominio.inscricao.carrinho.CarrinhoServico;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class AplicarCupomCarrinhoCasoDeUso {

    private final CarrinhoServico carrinhoServico;
    private final CupomServico cupomServico;

    public AplicarCupomCarrinhoCasoDeUso(CarrinhoServico carrinhoServico,
                                         CupomServico cupomServico) {
        Objects.requireNonNull(carrinhoServico);
        Objects.requireNonNull(cupomServico);
        this.carrinhoServico = carrinhoServico;
        this.cupomServico = cupomServico;
    }

    public Carrinho executar(UUID participanteId, String codigoCupom, String cpfParticipante) {
        BigDecimal desconto = cupomServico.validarEUtilizar(codigoCupom, cpfParticipante);
        return carrinhoServico.aplicarCupom(participanteId, codigoCupom, desconto);
    }
}