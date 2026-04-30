package br.voke.dominio.inscricao.carrinho;

import br.voke.dominio.compartilhado.EntidadeBase;
import br.voke.dominio.inscricao.excecao.CupomDuplicadoCarrinhoException;
import br.voke.dominio.inscricao.excecao.LimiteEventosCarrinhoException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Carrinho extends EntidadeBase<CarrinhoId> {

    public static final int MAX_EVENTOS = 2;
    private static final BigDecimal TAXA_CARTAO = new BigDecimal("0.05"); // 5%

    private final UUID participanteId;
    private final List<ItemCarrinho> itens;
    private String cupomAplicado;
    private BigDecimal descontoCupom;

    public Carrinho(CarrinhoId id, UUID participanteId) {
        super(id);
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        this.participanteId = participanteId;
        this.itens = new ArrayList<>();
        this.cupomAplicado = null;
        this.descontoCupom = BigDecimal.ZERO;
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

    public void aplicarCupom(String codigoCupom, BigDecimal desconto) {
        Objects.requireNonNull(codigoCupom, "Código do cupom é obrigatório");
        if (this.cupomAplicado != null) {
            throw new CupomDuplicadoCarrinhoException();
        }
        this.cupomAplicado = codigoCupom;
        this.descontoCupom = desconto;
    }

    public BigDecimal calcularTotal(MetodoPagamento metodo) {
        BigDecimal subtotal = itens.stream()
                .map(ItemCarrinho::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalComDesconto = subtotal.subtract(descontoCupom).max(BigDecimal.ZERO);
        if (metodo == MetodoPagamento.CARTAO_CREDITO) {
            BigDecimal taxa = totalComDesconto.multiply(TAXA_CARTAO).setScale(2, RoundingMode.HALF_UP);
            return totalComDesconto.add(taxa);
        }
        return totalComDesconto;
    }

    public void limpar() {
        itens.clear();
        cupomAplicado = null;
        descontoCupom = BigDecimal.ZERO;
    }

    public UUID getParticipanteId() { return participanteId; }
    public List<ItemCarrinho> getItens() { return Collections.unmodifiableList(itens); }
    public String getCupomAplicado() { return cupomAplicado; }
    public BigDecimal getDescontoCupom() { return descontoCupom; }
}