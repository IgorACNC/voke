package br.voke.dominio.inscricao.carrinho;

import br.voke.dominio.compartilhado.EntidadeBase;
import br.voke.dominio.inscricao.excecao.CupomDuplicadoCarrinhoException;
import br.voke.dominio.inscricao.excecao.LimiteEventosCarrinhoException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Carrinho extends EntidadeBase<CarrinhoId> {

    public static final int MAX_EVENTOS = 2;

    private final UUID participanteId;
    private final List<ItemCarrinho> itens;
    private String cupomAplicado;
    private EstrategiaDesconto estrategiaDesconto;

    public Carrinho(CarrinhoId id, UUID participanteId) {
        super(id);
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        this.participanteId = participanteId;
        this.itens = new ArrayList<>();
        this.cupomAplicado = null;
        this.estrategiaDesconto = null;
    }

    public void adicionarItem(ItemCarrinho item) {
        Objects.requireNonNull(item, "Item é obrigatório");
        long eventosDistintos = itens.stream()
                .map(ItemCarrinho::getEventoId)
                .distinct()
                .count();
        boolean eventoJaNoCarrinho = itens.stream()
                .anyMatch(i -> i.getEventoId().equals(item.getEventoId()));
        if (!eventoJaNoCarrinho && eventosDistintos >= MAX_EVENTOS) {
            throw new LimiteEventosCarrinhoException();
        }
        itens.add(item);
    }

    public void removerItem(UUID eventoId) {
        itens.removeIf(i -> i.getEventoId().equals(eventoId));
    }

    public void aplicarCupom(String codigoCupom, EstrategiaDesconto estrategia) {
        Objects.requireNonNull(codigoCupom, "Código do cupom é obrigatório");
        Objects.requireNonNull(estrategia, "Estratégia de desconto é obrigatória");
        if (this.cupomAplicado != null) {
            throw new CupomDuplicadoCarrinhoException();
        }
        this.cupomAplicado = codigoCupom;
        this.estrategiaDesconto = estrategia;
    }

    public BigDecimal calcularTotal(EstrategiaTaxa estrategiaTaxa) {
        Objects.requireNonNull(estrategiaTaxa, "Estratégia de taxa é obrigatória");
        BigDecimal subtotal = calcularSubtotal();
        BigDecimal desconto = estrategiaDesconto != null
                ? estrategiaDesconto.calcular(subtotal)
                : BigDecimal.ZERO;
        BigDecimal totalComDesconto = subtotal.subtract(desconto).max(BigDecimal.ZERO);
        return estrategiaTaxa.aplicar(totalComDesconto);
    }

    public void limpar() {
        itens.clear();
        cupomAplicado = null;
        estrategiaDesconto = null;
    }

    private BigDecimal calcularSubtotal() {
        return itens.stream()
                .map(ItemCarrinho::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public UUID getParticipanteId() { return participanteId; }
    public List<ItemCarrinho> getItens() { return Collections.unmodifiableList(itens); }
    public String getCupomAplicado() { return cupomAplicado; }

    public BigDecimal getDescontoCupom() {
        if (estrategiaDesconto == null) return BigDecimal.ZERO;
        return estrategiaDesconto.calcular(calcularSubtotal());
    }
}