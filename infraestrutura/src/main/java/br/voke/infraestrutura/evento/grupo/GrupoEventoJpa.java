package br.voke.infraestrutura.evento.grupo;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "grupos_evento")
public class GrupoEventoJpa {

    @Id
    private UUID id;
    private String nome;
    private String regras;
    private UUID eventoId;
    private UUID organizadorId;
    @ElementCollection
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
