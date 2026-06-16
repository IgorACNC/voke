package br.voke.dominio.evento.subgrupo;

public class SubgrupoFechadoException extends RuntimeException {
    public SubgrupoFechadoException() {
        super("Subgrupo é fechado. Envie uma solicitação de entrada.");
    }
}
