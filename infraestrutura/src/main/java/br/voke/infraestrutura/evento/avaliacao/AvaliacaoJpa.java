package br.voke.infraestrutura.evento.avaliacao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

@Entity
@Table(name = "avaliacoes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_avaliacao_participante_evento",
                columnNames = {"participante_id", "evento_id"}))
public class AvaliacaoJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "participante_id", nullable = false)
    private UUID participanteId;

    @Column(name = "evento_id", nullable = false)
    private UUID eventoId;

    @Column(nullable = false)
    private int nota;

    @Column(columnDefinition = "TEXT")
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
