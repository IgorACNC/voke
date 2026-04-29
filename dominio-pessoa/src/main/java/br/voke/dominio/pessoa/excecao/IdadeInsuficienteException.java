package br.voke.dominio.pessoa.excecao;

public class IdadeInsuficienteException extends RuntimeException {
    public IdadeInsuficienteException(String mensagem) {
        super(mensagem);
    }
}
