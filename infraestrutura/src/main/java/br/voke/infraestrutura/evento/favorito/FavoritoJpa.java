package br.voke.infraestrutura.evento.favorito;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "favoritos")
public class FavoritoJpa {

    @Id
    private UUID id;
    private UUID participanteId;
    private UUID eventoId;

    protected FavoritoJpa() {
    }

    public FavoritoJpa(UUID id, UUID participanteId, UUID eventoId) {
        this.id = id;
        this.participanteId = participanteId;
        this.eventoId = eventoId;
    }

    public UUID getId() { return id; }
    public UUID getParticipanteId() { return participanteId; }
    public UUID getEventoId() { return eventoId; }
}