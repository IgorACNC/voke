package br.voke.infraestrutura.pessoa.amizade;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "comunidades_amigos")
public class ComunidadeAmigosJpa {

    @Id
    private UUID id;
    private String nome;
    private UUID criadorId;
    @ElementCollection
    private Set<UUID> membros = new HashSet<>();
    @ElementCollection
    private Set<UUID> eventoCompartilhadoIds = new HashSet<>();

    protected ComunidadeAmigosJpa() {
    }

    public ComunidadeAmigosJpa(UUID id, String nome, UUID criadorId, Set<UUID> membros, Set<UUID> eventoCompartilhadoIds) {
        this.id = id;
        this.nome = nome;
        this.criadorId = criadorId;
        this.membros = new HashSet<>(membros);
        this.eventoCompartilhadoIds = new HashSet<>(eventoCompartilhadoIds);
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public UUID getCriadorId() { return criadorId; }
    public Set<UUID> getMembros() { return membros; }
    public Set<UUID> getEventoCompartilhadoIds() { return eventoCompartilhadoIds; }
}
