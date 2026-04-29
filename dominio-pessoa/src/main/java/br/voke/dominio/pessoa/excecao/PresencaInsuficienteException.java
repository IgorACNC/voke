package br.voke.dominio.pessoa.excecao;

public class PresencaInsuficienteException extends RuntimeException {
    public PresencaInsuficienteException() {
        super("O usuário não atingiu o mínimo de 5 eventos para ser parceiro");
    }
}
