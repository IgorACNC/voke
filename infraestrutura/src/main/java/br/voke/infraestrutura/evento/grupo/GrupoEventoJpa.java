package br.voke.infraestrutura.evento.grupo;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "grupos_evento")
public class GrupoEventoJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String regras;

    @Column(nullable = false)
    private UUID eventoId;

    @Column(nullable = false)
    private UUID organizadorId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "grupo_evento_membros",
            joinColumns = @JoinColumn(name = "grupo_id", nullable = false))
    @Column(name = "participante_id", nullable = false)
    private Set<UUID> membrosIds = new HashSet<>();

    protected GrupoEventoJpa() {
    }

    public GrupoEventoJpa(UUID id, String nome, String regras, UUID eventoId, UUID organizadorId, Set<UUID> membrosIds) {
        this.id = id;
        this.nome = nome;
        this.regras = regras;
        this.eventoId = eventoId;
        this.organizadorId = organizadorId;
        this.membrosIds = new HashSet<>(membrosIds);
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getRegras() { return regras; }
    public UUID getEventoId() { return eventoId; }
    public UUID getOrganizadorId() { return organizadorId; }
    public Set<UUID> getMembrosIds() { return membrosIds; }
}
