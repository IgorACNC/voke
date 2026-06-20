package br.voke.dominio.evento.subgrupo;

public class SubgrupoLotadoException extends RuntimeException {
    public SubgrupoLotadoException() {
        super("Subgrupo atingiu o limite máximo de membros");
    }
}
