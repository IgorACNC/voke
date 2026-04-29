package br.voke.infraestrutura.inscricao.carrinho;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "itens_carrinho")
public class ItemCarrinhoJpa {

    @Id
    @GeneratedValue
    private Long id;
    private UUID eventoId;
    private String nomeEvento;
    private int quantidade;
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