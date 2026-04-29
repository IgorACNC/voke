package br.voke.dominio.inscricao.excecao;

public class ConflitoDeAgendaException extends RuntimeException {
    public ConflitoDeAgendaException() {
        super("Você já possui uma inscrição neste horário");
    }
}
