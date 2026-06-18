package br.voke.dominio.inscricao.inscricao;

import br.voke.dominio.compartilhado.EntidadeBase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Inscricao extends EntidadeBase<InscricaoId> {

    private final UUID participanteId;
    private final UUID eventoId;
    private final BigDecimal valorPago;
    private final LocalDateTime dataInscricao;
    private StatusInscricao status;
    private boolean pontosCreditados;

    public Inscricao(InscricaoId id, UUID participanteId, UUID eventoId, BigDecimal valorPago) {
        super(id);
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        Objects.requireNonNull(eventoId, "Evento é obrigatório");
        Objects.requireNonNull(valorPago, "Valor pago é obrigatório");
        this.participanteId = participanteId;
        this.eventoId = eventoId;
        this.valorPago = valorPago;
        this.dataInscricao = LocalDateTime.now();
        this.status = StatusInscricao.CONFIRMADA;
        this.pontosCreditados = false;
    }

    public void marcarPontosCreditados() {
        this.pontosCreditados = true;
    }

    public boolean pontosJaCreditados() {
        return pontosCreditados;
    }

    public boolean fezCheckIn() {
        return status == StatusInscricao.CHECK_IN_REALIZADO;
    }

    public BigDecimal calcularDevolucao(LocalDateTime dataEvento) {
        long horasAteEvento = java.time.Duration.between(LocalDateTime.now(), dataEvento).toHours();
        if (horasAteEvento >= 168) {          // >= 7 dias → 100%
            return valorPago;
        } else if (horasAteEvento >= 48) {    // >= 48 horas → 50%
            return valorPago.multiply(BigDecimal.valueOf(0.5));
        } else if (horasAteEvento > 0) {      // evento ainda não começou → 25%
            return valorPago.multiply(BigDecimal.valueOf(0.25));
        } else {                              // evento já começou ou passou → sem estorno
            return BigDecimal.ZERO;
        }
    }

    public void cancelar() {
        this.status = StatusInscricao.CANCELADA;
    }

    public void realizarCheckIn() {
        this.status = StatusInscricao.CHECK_IN_REALIZADO;
    }

    public boolean estaConfirmada() { return status == StatusInscricao.CONFIRMADA; }

    public UUID getParticipanteId() { return participanteId; }
    public UUID getEventoId() { return eventoId; }
    public BigDecimal getValorPago() { return valorPago; }
    public LocalDateTime getDataInscricao() { return dataInscricao; }
    public StatusInscricao getStatus() { return status; }
    public boolean isPontosCreditados() { return pontosCreditados; }
}
