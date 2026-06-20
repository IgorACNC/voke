package br.voke.dominio.evento.excecao;

public class ColecaoNaoEncontradaException extends RuntimeException {
    public ColecaoNaoEncontradaException() {
        super("Coleção de favoritos não encontrada");
    }
}
