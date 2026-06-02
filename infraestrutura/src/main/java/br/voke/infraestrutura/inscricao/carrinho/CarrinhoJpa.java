package br.voke.infraestrutura.inscricao.carrinho;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "carrinhos")
public class CarrinhoJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID participanteId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "carrinho_id", nullable = false)
    private List<ItemCarrinhoJpa> itens = new ArrayList<>();

    @Column(length = 50)
    private String cupomAplicado;

    @Column(precision = 10, scale = 2)
    private BigDecimal descontoCupom;

    protected CarrinhoJpa() {
    }

    public CarrinhoJpa(UUID id, UUID participanteId, List<ItemCarrinhoJpa> itens, String cupomAplicado, BigDecimal descontoCupom) {
        this.id = id;
        this.participanteId = participanteId;
        this.itens = new ArrayList<>(itens);
        this.cupomAplicado = cupomAplicado;
        this.descontoCupom = descontoCupom;
    }

    public UUID getId() { return id; }
    public UUID getParticipanteId() { return participanteId; }
    public List<ItemCarrinhoJpa> getItens() { return itens; }
    public String getCupomAplicado() { return cupomAplicado; }
    public BigDecimal getDescontoCupom() { return descontoCupom; }
}