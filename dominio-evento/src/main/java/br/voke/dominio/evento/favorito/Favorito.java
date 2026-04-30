package br.voke.dominio.evento.favorito;

import br.voke.dominio.compartilhado.EntidadeBase;

import java.util.Objects;
import java.util.UUID;

public class Favorito extends EntidadeBase<FavoritoId> {

    private final UUID participanteId;
    private final UUID eventoId;

    public Favorito(FavoritoId id, UUID participanteId, UUID eventoId) {
        super(id);
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        Objects.requireNonNull(eventoId, "Evento é obrigatório");
        this.participanteId = participanteId;
        this.eventoId = eventoId;
    }

    public UUID getParticipanteId() { return participanteId; }
    public UUID getEventoId() { return eventoId; }
}