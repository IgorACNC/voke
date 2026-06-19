package br.voke.dominio.evento.excecao;

public class LimiteFaqExcedidoException extends RuntimeException {
    public LimiteFaqExcedidoException() {
        super("Este evento já possui o máximo de 20 perguntas frequentes cadastradas");
    }
}
