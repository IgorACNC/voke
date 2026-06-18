package br.voke.dominio.inscricao.carrinho;

import br.voke.dominio.compartilhado.EntidadeBase;
import br.voke.dominio.inscricao.excecao.CarrinhoExpiradoException;
import br.voke.dominio.inscricao.excecao.CupomDuplicadoCarrinhoException;
import br.voke.dominio.inscricao.excecao.LimiteEventosCarrinhoException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Carrinho extends EntidadeBase<CarrinhoId> {

    public static final int MAX_EVENTOS = 2;
    public static final long MINUTOS_EXPIRACAO = 15;

    private final UUID participanteId;
    private final List<ItemCarrinho> itens;
    private String cupomAplicado;
    private EstrategiaDesconto estrategiaDesconto;
    private LocalDateTime criadoEm;

    public Carrinho(CarrinhoId id, UUID participanteId) {
        super(id);
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        this.participanteId = participanteId;
        this.itens = new ArrayList<>();
        this.cupomAplicado = null;
        this.estrategiaDesconto = null;
        this.criadoEm = LocalDateTime.now();
    }

    public void adicionarItem(ItemCarrinho item) {
        Objects.requireNonNull(item, "Item é obrigatório");
        // Se o evento ja esta no carrinho, consolida quantidade (nao cria nova linha).
        // Garante invariante de "no maximo 1 item por evento" — necessario para que
        // removerItem(eventoId) remova exatamente 1 linha visivel ao usuario.
        for (ItemCarrinho existente : itens) {
            if (existente.getEventoId().equals(item.getEventoId())) {
                existente.atualizarQuantidade(existente.getQuantidade() + item.getQuantidade());
                this.criadoEm = LocalDateTime.now();
                return;
            }
        }
        long eventosDistintos = itens.stream()
                .map(ItemCarrinho::getEventoId)
                .distinct()
                .count();
        if (eventosDistintos >= MAX_EVENTOS) {
            throw new LimiteEventosCarrinhoException();
        }
        itens.add(item);
        this.criadoEm = LocalDateTime.now();
    }

    public void removerItem(UUID eventoId) {
        itens.removeIf(i -> i.getEventoId().equals(eventoId));
    }

    public void removerCupom() {
        this.cupomAplicado = null;
        this.estrategiaDesconto = null;
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
        criadoEm = LocalDateTime.now();
    }

    public void validarNaoExpirado() {
        if (criadoEm != null && criadoEm.plusMinutes(MINUTOS_EXPIRACAO).isBefore(LocalDateTime.now())) {
            throw new CarrinhoExpiradoException();
        }
    }

    public boolean isExpirado() {
        return criadoEm != null && criadoEm.plusMinutes(MINUTOS_EXPIRACAO).isBefore(LocalDateTime.now());
    }

    private BigDecimal calcularSubtotal() {
        return itens.stream()
                .map(ItemCarrinho::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public UUID getParticipanteId() { return participanteId; }
    public List<ItemCarrinho> getItens() { return Collections.unmodifiableList(itens); }
    public String getCupomAplicado() { return cupomAplicado; }
    public LocalDateTime getCriadoEm() { return criadoEm; }

    public BigDecimal getDescontoCupom() {
        if (estrategiaDesconto == null) return BigDecimal.ZERO;
        return estrategiaDesconto.calcular(calcularSubtotal());
    }
}