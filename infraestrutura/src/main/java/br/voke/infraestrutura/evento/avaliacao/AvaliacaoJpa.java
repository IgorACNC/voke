package br.voke.infraestrutura.evento.avaliacao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "avaliacoes")
public class AvaliacaoJpa {

    @Id
    private UUID id;
    private UUID participanteId;
    private UUID eventoId;
    private int nota;
    private String comentario;

    protected AvaliacaoJpa() {
    }

    public AvaliacaoJpa(UUID id, UUID participanteId, UUID eventoId, int nota, String comentario) {
        this.id = id;
        this.participanteId = participanteId;
        this.eventoId = eventoId;
        this.nota = nota;
        this.comentario = comentario;
    }

    public UUID getId() { return id; }
    public UUID getParticipanteId() { return participanteId; }
    public UUID getEventoId() { return eventoId; }
    public int getNota() { return nota; }
    public String getComentario() { return comentario; }
}
