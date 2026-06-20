package br.voke.aplicacao.inscricao;

import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.inscricao.carrinho.Carrinho;
import br.voke.dominio.inscricao.carrinho.CarrinhoServico;
import br.voke.dominio.inscricao.carrinho.CupomGateway;
import br.voke.dominio.inscricao.carrinho.ItemCarrinho;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AplicarCupomCarrinhoCasoDeUso {

    private final CarrinhoServico carrinhoServico;
    private final CupomGateway cupomGateway;
    private final EventoRepositorio eventoRepositorio;

    public AplicarCupomCarrinhoCasoDeUso(CarrinhoServico carrinhoServico,
                                         CupomGateway cupomGateway,
                                         EventoRepositorio eventoRepositorio) {
        Objects.requireNonNull(carrinhoServico);
        Objects.requireNonNull(cupomGateway);
        Objects.requireNonNull(eventoRepositorio);
        this.carrinhoServico = carrinhoServico;
        this.cupomGateway = cupomGateway;
        this.eventoRepositorio = eventoRepositorio;
    }

    public Carrinho executar(UUID participanteId, String codigoCupom, String cpfParticipante) {
        Carrinho carrinhoAtual = carrinhoServico.obterOuCriar(participanteId);
        Map<UUID, UUID> organizadorPorEvento = new HashMap<>();
        for (ItemCarrinho item : carrinhoAtual.getItens()) {
            eventoRepositorio.buscarPorId(new EventoId(item.getEventoId()))
                    .ifPresent(ev -> organizadorPorEvento.put(item.getEventoId(),
                            ev.getOrganizadorId()));
        }
        BigDecimal subtotal = carrinhoAtual.getItens().stream()
                .map(ItemCarrinho::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal desconto = cupomGateway.validarEUtilizar(codigoCupom, cpfParticipante,
                organizadorPorEvento, subtotal);
        return carrinhoServico.aplicarCupom(participanteId, codigoCupom, desconto);
    }
}