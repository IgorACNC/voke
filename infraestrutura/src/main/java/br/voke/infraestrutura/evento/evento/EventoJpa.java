package br.voke.infraestrutura.evento.evento;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import br.voke.dominio.evento.evento.StatusEvento;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "eventos")
public class EventoJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, length = 255)
    private String local;

    @Column(nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(nullable = false)
    private LocalDateTime dataHoraFim;

    @Column(nullable = false)
    private int capacidadeMaxima;

    @Column(nullable = false)
    private UUID organizadorId;

    @Column(nullable = false)
    private int idadeMinima;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusEvento status;

    @Embedded
    private LoteJpa loteAtual;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "evento_tags", joinColumns = @JoinColumn(name = "evento_id"))
    @Column(name = "tag", length = 50)
    private Set<String> tags = new HashSet<>();

    protected EventoJpa() {
    }

    public EventoJpa(UUID id, String nome, String descricao, String local, LocalDateTime dataHoraInicio,
                     LocalDateTime dataHoraFim, int capacidadeMaxima, UUID organizadorId,
                     int idadeMinima, StatusEvento status, LoteJpa loteAtual, Set<String> tags) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.local = local;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.capacidadeMaxima = capacidadeMaxima;
        this.organizadorId = organizadorId;
        this.idadeMinima = idadeMinima;
        this.status = status;
        this.loteAtual = loteAtual;
        this.tags = tags != null ? new HashSet<>(tags) : new HashSet<>();
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getLocal() { return local; }
    public LocalDateTime getDataHoraInicio() { return dataHoraInicio; }
    public LocalDateTime getDataHoraFim() { return dataHoraFim; }
    public int getCapacidadeMaxima() { return capacidadeMaxima; }
    public UUID getOrganizadorId() { return organizadorId; }
    public int getIdadeMinima() { return idadeMinima; }
    public StatusEvento getStatus() { return status; }
    public LoteJpa getLoteAtual() { return loteAtual; }
    public Set<String> getTags() { return tags; }
}
