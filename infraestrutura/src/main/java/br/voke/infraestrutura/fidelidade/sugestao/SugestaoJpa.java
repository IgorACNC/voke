package br.voke.infraestrutura.fidelidade.sugestao;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import br.voke.dominio.fidelidade.sugestao.StatusSugestao;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sugestoes")
public class SugestaoJpa {

    @Id
    private UUID id;
    private UUID participanteId;
    private UUID eventoId;
    @Enumerated(EnumType.STRING)
    private StatusSugestao status;
    private LocalDateTime criadaEm;

    protected SugestaoJpa() {
    }

    public SugestaoJpa(UUID id, UUID participanteId, UUID eventoId, StatusSugestao status, LocalDateTime criadaEm) {
        this.id = id;
        this.participanteId = participanteId;
        this.eventoId = eventoId;
        this.status = status;
        this.criadaEm = criadaEm;
    }

    public UUID getId() { return id; }
    public UUID getParticipanteId() { return participanteId; }
    public UUID getEventoId() { return eventoId; }
    public StatusSugestao getStatus() { return status; }
    public LocalDateTime getCriadaEm() { return criadaEm; }
}
