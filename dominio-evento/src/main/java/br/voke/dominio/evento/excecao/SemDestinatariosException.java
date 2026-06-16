package br.voke.dominio.evento.excecao;

public class SemDestinatariosException extends RuntimeException {
    public SemDestinatariosException(String mensagem) {
        super(mensagem);
    }
}
