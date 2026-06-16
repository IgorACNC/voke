package br.voke.dominio.evento.subgrupo;

public class ParticipanteNaoEhMembroException extends RuntimeException {
    public ParticipanteNaoEhMembroException() {
        super("Participante precisa ser membro do subgrupo para esta ação");
    }
}
