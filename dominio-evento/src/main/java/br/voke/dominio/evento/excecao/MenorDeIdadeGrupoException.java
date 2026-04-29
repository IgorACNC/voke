package br.voke.dominio.evento.excecao;

public class MenorDeIdadeGrupoException extends RuntimeException {
    public MenorDeIdadeGrupoException() {
        super("Menores de idade não podem participar do grupo");
    }
}
