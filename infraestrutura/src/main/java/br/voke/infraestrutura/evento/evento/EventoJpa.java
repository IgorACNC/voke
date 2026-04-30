package br.voke.infraestrutura.evento.evento;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import br.voke.dominio.evento.evento.StatusEvento;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "eventos")
public class EventoJpa {

    @Id
    private UUID id;
    private String nome;
    private String descricao;
    private String local;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private int capacidadeMaxima;
    private UUID organizadorId;
    private int idadeMinima;
    @Enumerated(EnumType.STRING)
    private StatusEvento status;
    @Embedded
    private LoteJpa loteAtual;

    protected EventoJpa() {
    }

    public EventoJpa(UUID id, String nome, String descricao, String local, LocalDateTime dataHoraInicio,
                     LocalDateTime dataHoraFim, int capacidadeMaxima, UUID organizadorId,
                     int idadeMinima, StatusEvento status, LoteJpa loteAtual) {
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
}
