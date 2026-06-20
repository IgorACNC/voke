package br.voke.infraestrutura.pessoa.amizade;

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
@Table(name = "comunidades_amigos")
public class ComunidadeAmigosJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(nullable = false)
    private UUID criadorId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "comunidade_membros",
            joinColumns = @JoinColumn(name = "comunidade_id", nullable = false))
    @Column(name = "participante_id", nullable = false)
    private Set<UUID> membros = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "comunidade_eventos_compartilhados",
            joinColumns = @JoinColumn(name = "comunidade_id", nullable = false))
    @Column(name = "evento_id", nullable = false)
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
