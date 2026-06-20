package br.voke.dominio.evento.excecao;

public class LimiteEdicoesAtingidoException extends RuntimeException {
    public LimiteEdicoesAtingidoException(String mensagem) {
        super(mensagem);
    }
}
