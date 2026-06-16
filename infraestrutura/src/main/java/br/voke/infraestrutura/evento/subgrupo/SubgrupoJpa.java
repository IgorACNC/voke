package br.voke.infraestrutura.evento.subgrupo;

import br.voke.dominio.evento.subgrupo.CategoriaSubgrupo;
import br.voke.dominio.evento.subgrupo.TipoSubgrupo;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "subgrupos")
public class SubgrupoJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(columnDefinition = "TEXT")
    private String regras;

    @Column(nullable = false)
    private UUID grupoEventoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaSubgrupo categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoSubgrupo tipo;

    @Column(nullable = false)
    private int limiteMembros;

    @Column
    private UUID moderadorId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "subgrupo_membros",
            joinColumns = @JoinColumn(name = "subgrupo_id", nullable = false))
    @Column(name = "participante_id", nullable = false)
    private Set<UUID> membrosIds = new HashSet<>();

    protected SubgrupoJpa() {
    }

    public SubgrupoJpa(UUID id, String nome, String descricao, String regras, UUID grupoEventoId,
                       CategoriaSubgrupo categoria, TipoSubgrupo tipo, int limiteMembros,
                       UUID moderadorId, Set<UUID> membrosIds) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.regras = regras;
        this.grupoEventoId = grupoEventoId;
        this.categoria = categoria;
        this.tipo = tipo;
        this.limiteMembros = limiteMembros;
        this.moderadorId = moderadorId;
        this.membrosIds = new HashSet<>(membrosIds);
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getRegras() { return regras; }
    public UUID getGrupoEventoId() { return grupoEventoId; }
    public CategoriaSubgrupo getCategoria() { return categoria; }
    public TipoSubgrupo getTipo() { return tipo; }
    public int getLimiteMembros() { return limiteMembros; }
    public UUID getModeradorId() { return moderadorId; }
    public Set<UUID> getMembrosIds() { return membrosIds; }
}
