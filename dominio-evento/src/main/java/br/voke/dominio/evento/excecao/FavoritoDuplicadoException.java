package br.voke.dominio.evento.excecao;

public class FavoritoDuplicadoException extends RuntimeException {
    public FavoritoDuplicadoException() {
        super("Este evento já está na sua lista de favoritos");
    }
}