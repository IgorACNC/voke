package br.voke.dominio.pessoa.excecao;

public class EmailDuplicadoException extends RuntimeException {
    public EmailDuplicadoException() {
        super("E-mail já cadastrado");
    }
}
