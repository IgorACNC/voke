package br.voke.infraestrutura.evento.cupom;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import br.voke.dominio.evento.cupom.TipoDesconto;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "cupons")
public class CupomJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal desconto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_desconto", nullable = false, length = 20,
            columnDefinition = "VARCHAR(20) NOT NULL DEFAULT 'FIXO'")
    private TipoDesconto tipoDesconto;

    @Column(nullable = true)
    private UUID organizadorId;

    @Column(nullable = true)
    private UUID eventoId;

    @Column(nullable = false)
    private int quantidadeMaxima;

    @Column(nullable = false)
    private boolean ativo;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cupom_cpfs_utilizados",
            joinColumns = @JoinColumn(name = "cupom_id", nullable = false))
    @Column(name = "cpf", nullable = false, length = 14)
    private Set<String> cpfsUtilizados = new HashSet<>();

    protected CupomJpa() {
    }

    public CupomJpa(UUID id, String codigo, BigDecimal desconto, TipoDesconto tipoDesconto,
                    UUID organizadorId, UUID eventoId, int quantidadeMaxima, boolean ativo,
                    Set<String> cpfsUtilizados) {
        this.id = id;
        this.codigo = codigo;
        this.desconto = desconto;
        this.tipoDesconto = tipoDesconto;
        this.organizadorId = organizadorId;
        this.eventoId = eventoId;
        this.quantidadeMaxima = quantidadeMaxima;
        this.ativo = ativo;
        this.cpfsUtilizados = new HashSet<>(cpfsUtilizados);
    }

    public UUID getId() { return id; }
    public String getCodigo() { return codigo; }
    public BigDecimal getDesconto() { return desconto; }
    public TipoDesconto getTipoDesconto() { return tipoDesconto; }
    public UUID getOrganizadorId() { return organizadorId; }
    public UUID getEventoId() { return eventoId; }
    public int getQuantidadeMaxima() { return quantidadeMaxima; }
    public boolean isAtivo() { return ativo; }
    public Set<String> getCpfsUtilizados() { return cpfsUtilizados; }
}
