package br.voke.dominio.evento.excecao;

public class EventoNaoNaColecaoException extends RuntimeException {
    public EventoNaoNaColecaoException() {
        super("Este evento não está nessa coleção");
    }
}
