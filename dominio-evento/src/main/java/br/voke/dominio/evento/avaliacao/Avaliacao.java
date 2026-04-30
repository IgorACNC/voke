package br.voke.dominio.evento.avaliacao;

import br.voke.dominio.compartilhado.EntidadeBase;

import java.util.Objects;
import java.util.UUID;

public class Avaliacao extends EntidadeBase<AvaliacaoId> {

    private final UUID participanteId;
    private final UUID eventoId;
    private int nota;
    private String comentario;

    public Avaliacao(AvaliacaoId id, UUID participanteId, UUID eventoId, int nota, String comentario) {
        super(id);
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        Objects.requireNonNull(eventoId, "Evento é obrigatório");
        validarNota(nota);
        this.participanteId = participanteId;
        this.eventoId = eventoId;
        this.nota = nota;
        this.comentario = comentario;
    }

    public void editar(int novaNota, String novoComentario) {
        validarNota(novaNota);
        this.nota = novaNota;
        this.comentario = novoComentario;
    }

    private void validarNota(int nota) {
        if (nota < 1 || nota > 5) {
            throw new IllegalArgumentException("Nota deve ser entre 1 e 5");
        }
    }

    public UUID getParticipanteId() { return participanteId; }
    public UUID getEventoId() { return eventoId; }
    public int getNota() { return nota; }
    public String getComentario() { return comentario; }
}
