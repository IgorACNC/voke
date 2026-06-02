package br.voke.infraestrutura.fidelidade.recompensa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "recompensas")
public class RecompensaJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private int custoEmPontos;

    @Column(nullable = false)
    private int estoqueTotal;

    @Column(nullable = false)
    private int estoqueResgatado;

    @Column(nullable = false)
    private UUID organizadorId;

    @Column(nullable = false)
    private LocalDateTime ultimaAlteracaoValor;

    @Column(nullable = false)
    private boolean ativa;

    protected RecompensaJpa() {
    }

    public RecompensaJpa(UUID id, String nome, String descricao, int custoEmPontos, int estoqueTotal,
                         int estoqueResgatado, UUID organizadorId, LocalDateTime ultimaAlteracaoValor, boolean ativa) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.custoEmPontos = custoEmPontos;
        this.estoqueTotal = estoqueTotal;
        this.estoqueResgatado = estoqueResgatado;
        this.organizadorId = organizadorId;
        this.ultimaAlteracaoValor = ultimaAlteracaoValor;
        this.ativa = ativa;
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public int getCustoEmPontos() { return custoEmPontos; }
    public int getEstoqueTotal() { return estoqueTotal; }
    public int getEstoqueResgatado() { return estoqueResgatado; }
    public UUID getOrganizadorId() { return organizadorId; }
    public LocalDateTime getUltimaAlteracaoValor() { return ultimaAlteracaoValor; }
    public boolean isAtiva() { return ativa; }
}
