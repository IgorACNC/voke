package br.voke.dominio.evento.estatistica;

public class AcessoDashboardNegadoException extends RuntimeException {
    public AcessoDashboardNegadoException() {
        super("Acesso negado: voce nao tem permissao para acessar este dashboard");
    }

    public AcessoDashboardNegadoException(String mensagem) {
        super(mensagem);
    }
}
