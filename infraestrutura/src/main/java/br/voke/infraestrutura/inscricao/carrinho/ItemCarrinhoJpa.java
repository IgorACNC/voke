package br.voke.infraestrutura.inscricao.carrinho;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "itens_carrinho")
public class ItemCarrinhoJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID eventoId;

    @Column(nullable = false, length = 255)
    private String nomeEvento;

    @Column(nullable = false)
    private int quantidade;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    protected ItemCarrinhoJpa() {
    }

    public ItemCarrinhoJpa(UUID eventoId, String nomeEvento, int quantidade, BigDecimal precoUnitario) {
        this.eventoId = eventoId;
        this.nomeEvento = nomeEvento;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public UUID getEventoId() { return eventoId; }
    public String getNomeEvento() { return nomeEvento; }
    public int getQuantidade() { return quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
}