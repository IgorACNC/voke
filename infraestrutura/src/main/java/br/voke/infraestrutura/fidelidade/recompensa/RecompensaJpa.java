package br.voke.infraestrutura.fidelidade.recompensa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "recompensas")
public class RecompensaJpa {

    @Id
    private UUID id;
    private String nome;
    private String descricao;
    private int custoEmPontos;
    private int estoqueTotal;
    private int estoqueResgatado;
    private UUID organizadorId;
    private LocalDateTime ultimaAlteracaoValor;
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
