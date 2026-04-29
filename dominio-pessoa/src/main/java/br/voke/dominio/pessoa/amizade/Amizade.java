package br.voke.dominio.pessoa.amizade;

import br.voke.dominio.compartilhado.EntidadeBase;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.Objects;

public class Amizade extends EntidadeBase<AmizadeId> {

    private final ParticipanteId solicitanteId;
    private final ParticipanteId receptorId;
    private StatusAmizade status;

    public Amizade(AmizadeId id, ParticipanteId solicitanteId, ParticipanteId receptorId) {
        super(id);
        Objects.requireNonNull(solicitanteId, "Solicitante é obrigatório");
        Objects.requireNonNull(receptorId, "Receptor é obrigatório");
        this.solicitanteId = solicitanteId;
        this.receptorId = receptorId;
        this.status = StatusAmizade.PENDENTE;
    }

    public void aceitar() {
        this.status = StatusAmizade.ATIVA;
    }

    public void recusar() {
        this.status = StatusAmizade.RECUSADA;
    }

    public void desfazer() {
        this.status = StatusAmizade.DESFEITA;
    }

    public boolean estaAtiva() {
        return status == StatusAmizade.ATIVA;
    }

    public ParticipanteId getSolicitanteId() { return solicitanteId; }
    public ParticipanteId getReceptorId() { return receptorId; }
    public StatusAmizade getStatus() { return status; }
}
