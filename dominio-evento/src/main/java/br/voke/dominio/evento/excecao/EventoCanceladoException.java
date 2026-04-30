package br.voke.dominio.evento.excecao;

public class EventoCanceladoException extends RuntimeException {
    public EventoCanceladoException(String mensagem) {
        super(mensagem);
    }
}
