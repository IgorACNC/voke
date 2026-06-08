package br.voke.dominio.pessoa.excecao;

public class ParticipantesNaoSaoAmigosException extends RuntimeException {
    public ParticipantesNaoSaoAmigosException() {
        super("Apenas amigos podem conversar em chat privado");
    }
}
