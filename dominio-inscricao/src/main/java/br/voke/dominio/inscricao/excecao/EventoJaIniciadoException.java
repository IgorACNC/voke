package br.voke.dominio.inscricao.excecao;

public class EventoJaIniciadoException extends RuntimeException {
    public EventoJaIniciadoException() {
        super("Inscrições encerradas: o evento já começou");
    }
}
