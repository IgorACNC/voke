package br.voke.infraestrutura.inscricao.inscricao;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import br.voke.dominio.inscricao.inscricao.StatusInscricao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inscricoes")
public class InscricaoJpa {

    @Id
    private UUID id;
    private UUID participanteId;
    private UUID eventoId;
    private BigDecimal valorPago;
    private LocalDateTime dataInscricao;
    @Enumerated(EnumType.STRING)
    private StatusInscricao status;

    protected InscricaoJpa() {
    }

    public InscricaoJpa(UUID id, UUID participanteId, UUID eventoId, BigDecimal valorPago,
                        LocalDateTime dataInscricao, StatusInscricao status) {
        this.id = id;
        this.participanteId = participanteId;
        this.eventoId = eventoId;
        this.valorPago = valorPago;
        this.dataInscricao = dataInscricao;
        this.status = status;
    }

    public UUID getId() { return id; }
    public UUID getParticipanteId() { return participanteId; }
    public UUID getEventoId() { return eventoId; }
    public BigDecimal getValorPago() { return valorPago; }
    public LocalDateTime getDataInscricao() { return dataInscricao; }
    public StatusInscricao getStatus() { return status; }
}
