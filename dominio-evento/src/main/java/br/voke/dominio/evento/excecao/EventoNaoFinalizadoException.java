package br.voke.dominio.evento.excecao;

public class EventoNaoFinalizadoException extends RuntimeException {
    public EventoNaoFinalizadoException() {
        super("Só é possível avaliar após o encerramento do evento");
    }
}
