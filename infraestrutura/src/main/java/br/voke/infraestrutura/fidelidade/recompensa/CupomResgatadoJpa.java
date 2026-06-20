package br.voke.infraestrutura.fidelidade.recompensa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cupons_resgatados")
public class CupomResgatadoJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID participanteId;

    @Column(nullable = false, length = 80)
    private String codigoCupom;

    @Column(nullable = false)
    private UUID recompensaId;

    @Column(nullable = false, length = 255)
    private String recompensaNome;

    @Column(nullable = true, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = true)
    private UUID organizadorId;

    @Column(nullable = false)
    private LocalDateTime dataResgate;

    protected CupomResgatadoJpa() {}

    public CupomResgatadoJpa(UUID id, UUID participanteId, String codigoCupom,
                              UUID recompensaId, String recompensaNome, BigDecimal valor,
                              UUID organizadorId, LocalDateTime dataResgate) {
        this.id = id;
        this.participanteId = participanteId;
        this.codigoCupom = codigoCupom;
        this.recompensaId = recompensaId;
        this.recompensaNome = recompensaNome;
        this.valor = valor;
        this.organizadorId = organizadorId;
        this.dataResgate = dataResgate;
    }

    public UUID getId() { return id; }
    public UUID getParticipanteId() { return participanteId; }
    public String getCodigoCupom() { return codigoCupom; }
    public UUID getRecompensaId() { return recompensaId; }
    public String getRecompensaNome() { return recompensaNome; }
    public BigDecimal getValor() { return valor; }
    public UUID getOrganizadorId() { return organizadorId; }
    public LocalDateTime getDataResgate() { return dataResgate; }
}
