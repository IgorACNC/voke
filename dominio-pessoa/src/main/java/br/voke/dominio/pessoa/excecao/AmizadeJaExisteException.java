package br.voke.dominio.pessoa.excecao;

public class AmizadeJaExisteException extends RuntimeException {
    public AmizadeJaExisteException() {
        super("Já existe uma solicitação de amizade entre esses participantes");
    }
}
