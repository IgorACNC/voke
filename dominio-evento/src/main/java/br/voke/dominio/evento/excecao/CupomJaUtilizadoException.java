package br.voke.dominio.evento.excecao;

public class CupomJaUtilizadoException extends RuntimeException {
    public CupomJaUtilizadoException() {
        super("Este cupom já foi utilizado pelo seu CPF");
    }
}
