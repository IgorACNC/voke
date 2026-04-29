package br.voke.dominio.pessoa.excecao;

public class VinculoDeAmizadeNecessarioException extends RuntimeException {
    public VinculoDeAmizadeNecessarioException() {
        super("Confirme uma amizade antes de criar um grupo");
    }
}
