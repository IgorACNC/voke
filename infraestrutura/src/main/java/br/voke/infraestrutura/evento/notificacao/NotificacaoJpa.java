package br.voke.infraestrutura.evento.notificacao;

import br.voke.dominio.evento.notificacao.StatusNotificacao;
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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "notificacoes")
public class NotificacaoJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID eventoId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Column(nullable = false)
    private LocalDateTime dataEnvio;

    @Column(nullable = false)
    private boolean editada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusNotificacao status;

    @Column
    private LocalDateTime dataAgendamento;

    @Column(nullable = false)
    private int contadorEdicoes;

    @Column(length = 100)
    private String criterioSegmentacao;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "notificacao_destinatarios",
            joinColumns = @JoinColumn(name = "notificacao_id"))
    @Column(name = "participante_id")
    private Set<UUID> destinatariosIds = new HashSet<>();

    protected NotificacaoJpa() {
    }

    public NotificacaoJpa(UUID id, UUID eventoId, String conteudo, LocalDateTime dataEnvio, boolean editada,
                          StatusNotificacao status, LocalDateTime dataAgendamento, int contadorEdicoes,
                          String criterioSegmentacao, Set<UUID> destinatariosIds) {
        this.id = id;
        this.eventoId = eventoId;
        this.conteudo = conteudo;
        this.dataEnvio = dataEnvio;
        this.editada = editada;
        this.status = status;
        this.dataAgendamento = dataAgendamento;
        this.contadorEdicoes = contadorEdicoes;
        this.criterioSegmentacao = criterioSegmentacao;
        this.destinatariosIds = destinatariosIds != null ? new HashSet<>(destinatariosIds) : new HashSet<>();
    }

    public UUID getId() { return id; }
    public UUID getEventoId() { return eventoId; }
    public String getConteudo() { return conteudo; }
    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public boolean isEditada() { return editada; }
    public StatusNotificacao getStatus() { return status; }
    public LocalDateTime getDataAgendamento() { return dataAgendamento; }
    public int getContadorEdicoes() { return contadorEdicoes; }
    public String getCriterioSegmentacao() { return criterioSegmentacao; }
    public Set<UUID> getDestinatariosIds() { return destinatariosIds; }
}
