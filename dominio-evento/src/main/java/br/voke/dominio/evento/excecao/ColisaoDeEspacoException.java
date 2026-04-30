package br.voke.dominio.evento.excecao;

public class ColisaoDeEspacoException extends RuntimeException {
    public ColisaoDeEspacoException() {
        super("Já existe um evento neste local, data e horário");
    }
}
