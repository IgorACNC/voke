package br.voke.infraestrutura.evento.notificacao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notificacoes")
public class NotificacaoJpa {

    @Id
    private UUID id;
    private UUID eventoId;
    private String conteudo;
    private LocalDateTime dataEnvio;
    private boolean editada;

    protected NotificacaoJpa() {
    }

    public NotificacaoJpa(UUID id, UUID eventoId, String conteudo, LocalDateTime dataEnvio, boolean editada) {
        this.id = id;
        this.eventoId = eventoId;
        this.conteudo = conteudo;
        this.dataEnvio = dataEnvio;
        this.editada = editada;
    }

    public UUID getId() { return id; }
    public UUID getEventoId() { return eventoId; }
    public String getConteudo() { return conteudo; }
    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public boolean isEditada() { return editada; }
}
