package br.voke.infraestrutura.evento.favorito;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

@Entity
@Table(name = "favoritos",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_favorito_participante_evento",
                columnNames = {"participante_id", "evento_id"}))
public class FavoritoJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "participante_id", nullable = false)
    private UUID participanteId;

    @Column(name = "evento_id", nullable = false)
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
