package br.voke.dominio.evento.excecao;

public class StatusEventoInvalidoException extends RuntimeException {
    public StatusEventoInvalidoException(String mensagem) {
        super(mensagem);
    }
}