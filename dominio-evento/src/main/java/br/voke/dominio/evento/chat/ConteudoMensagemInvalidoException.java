package br.voke.dominio.evento.chat;

public class ConteudoMensagemInvalidoException extends RuntimeException {
    public ConteudoMensagemInvalidoException(String mensagem) {
        super(mensagem);
    }
}
