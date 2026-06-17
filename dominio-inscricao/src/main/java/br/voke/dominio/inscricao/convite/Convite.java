package br.voke.dominio.inscricao.convite;

import br.voke.dominio.compartilhado.EntidadeBase;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Convite extends EntidadeBase<ConviteId> {

    private final UUID remetenteId;
    private final UUID destinatarioId;
    private final UUID eventoId;
    private StatusConvite status;
    private final LocalDateTime criadoEm;
    private final LocalDateTime expiraEm;

    public Convite(ConviteId id, UUID remetenteId, UUID destinatarioId, UUID eventoId) {
        super(id);
        Objects.requireNonNull(remetenteId, "Remetente é obrigatório");
        Objects.requireNonNull(destinatarioId, "Destinatário é obrigatório");
        Objects.requireNonNull(eventoId, "Evento é obrigatório");
        this.remetenteId = remetenteId;
        this.destinatarioId = destinatarioId;
        this.eventoId = eventoId;
        this.status = StatusConvite.PENDENTE;
        this.criadoEm = LocalDateTime.now();
        this.expiraEm = this.criadoEm.plusHours(48);
    }

    public boolean expirou() {
        return status == StatusConvite.EXPIRADO
                || (status == StatusConvite.PENDENTE && LocalDateTime.now().isAfter(expiraEm));
    }

    public StatusConvite getStatusEfetivo() {
        return expirou() && status == StatusConvite.PENDENTE ? StatusConvite.EXPIRADO : status;
    }

    public void aceitar() {
        if (expirou()) throw new IllegalStateException("Convite expirado");
        if (status != StatusConvite.PENDENTE)
            throw new IllegalStateException("Convite não está pendente");
        this.status = StatusConvite.ACEITO;
    }

    public void rejeitar() {
        if (expirou()) throw new IllegalStateException("Convite expirado");
        if (status != StatusConvite.PENDENTE)
            throw new IllegalStateException("Convite não está pendente");
        this.status = StatusConvite.REJEITADO;
    }

    public void cancelar() {
        if (status != StatusConvite.PENDENTE)
            throw new IllegalStateException("Somente convites pendentes podem ser cancelados");
        this.status = StatusConvite.CANCELADO;
    }

    public UUID getRemetenteId() { return remetenteId; }
    public UUID getDestinatarioId() { return destinatarioId; }
    public UUID getEventoId() { return eventoId; }
    public StatusConvite getStatus() { return status; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getExpiraEm() { return expiraEm; }
}
