package br.voke.dominio.evento.chat;

public class AcessoChatCanalNegadoException extends RuntimeException {
    public AcessoChatCanalNegadoException() {
        super("Acesso negado: voce nao tem permissao para acessar este canal de chat");
    }

    public AcessoChatCanalNegadoException(String mensagem) {
        super(mensagem);
    }
}
