package br.voke.dominio.inscricao.carrinho;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class ItemCarrinho {

    private final UUID eventoId;
    private final String nomeEvento;
    private int quantidade;
    private BigDecimal precoUnitario;

    public ItemCarrinho(UUID eventoId, String nomeEvento, int quantidade, BigDecimal precoUnitario) {
        Objects.requireNonNull(eventoId, "Evento é obrigatório");
        Objects.requireNonNull(nomeEvento, "Nome do evento é obrigatório");
        Objects.requireNonNull(precoUnitario, "Preço unitário é obrigatório");
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        this.eventoId = eventoId;
        this.nomeEvento = nomeEvento;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public void atualizarQuantidade(int novaQuantidade) {
        if (novaQuantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        this.quantidade = novaQuantidade;
    }

    public void atualizarPreco(BigDecimal novoPreco) {
        Objects.requireNonNull(novoPreco, "Preço é obrigatório");
        this.precoUnitario = novoPreco;
    }

    public UUID getEventoId() { return eventoId; }
    public String getNomeEvento() { return nomeEvento; }
    public int getQuantidade() { return quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
}