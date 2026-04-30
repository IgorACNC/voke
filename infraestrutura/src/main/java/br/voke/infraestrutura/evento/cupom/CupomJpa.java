package br.voke.infraestrutura.evento.cupom;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "cupons")
public class CupomJpa {

    @Id
    private UUID id;
    private String codigo;
    private BigDecimal desconto;
    private UUID organizadorId;
    private UUID eventoId;
    private int quantidadeMaxima;
    @ElementCollection
    private Set<String> cpfsUtilizados = new HashSet<>();

    protected CupomJpa() {
    }

    public CupomJpa(UUID id, String codigo, BigDecimal desconto, UUID organizadorId,
                    UUID eventoId, int quantidadeMaxima, Set<String> cpfsUtilizados) {
        this.id = id;
        this.codigo = codigo;
        this.desconto = desconto;
        this.organizadorId = organizadorId;
        this.eventoId = eventoId;
        this.quantidadeMaxima = quantidadeMaxima;
        this.cpfsUtilizados = new HashSet<>(cpfsUtilizados);
    }

    public UUID getId() { return id; }
    public String getCodigo() { return codigo; }
    public BigDecimal getDesconto() { return desconto; }
    public UUID getOrganizadorId() { return organizadorId; }
    public UUID getEventoId() { return eventoId; }
    public int getQuantidadeMaxima() { return quantidadeMaxima; }
    public Set<String> getCpfsUtilizados() { return cpfsUtilizados; }
}
