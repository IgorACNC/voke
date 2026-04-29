package br.voke.dominio.pessoa.excecao;

public class CpfDuplicadoException extends RuntimeException {
    public CpfDuplicadoException() {
        super("CPF já cadastrado");
    }
}
