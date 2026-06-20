package br.voke.infraestrutura.inscricao.convite;

import br.voke.dominio.inscricao.convite.StatusConvite;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "convites")
public class ConviteJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID remetenteId;

    @Column(nullable = false)
    private UUID destinatarioId;

    @Column(nullable = false)
    private UUID eventoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusConvite status;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    private LocalDateTime expiraEm;

    protected ConviteJpa() {}

    public ConviteJpa(UUID id, UUID remetenteId, UUID destinatarioId, UUID eventoId,
                      StatusConvite status, LocalDateTime criadoEm, LocalDateTime expiraEm) {
        this.id = id;
        this.remetenteId = remetenteId;
        this.destinatarioId = destinatarioId;
        this.eventoId = eventoId;
        this.status = status;
        this.criadoEm = criadoEm;
        this.expiraEm = expiraEm;
    }

    public UUID getId() { return id; }
    public UUID getRemetenteId() { return remetenteId; }
    public UUID getDestinatarioId() { return destinatarioId; }
    public UUID getEventoId() { return eventoId; }
    public StatusConvite getStatus() { return status; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getExpiraEm() { return expiraEm; }
}
