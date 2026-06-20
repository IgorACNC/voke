package br.voke.dominio.evento.excecao;

public class AgendamentoInvalidoException extends RuntimeException {
    public AgendamentoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
