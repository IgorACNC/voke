package br.voke.dominio.evento.excecao;

public class EventoJaNaColecaoException extends RuntimeException {
    public EventoJaNaColecaoException() {
        super("Este evento já está nessa coleção");
    }
}
