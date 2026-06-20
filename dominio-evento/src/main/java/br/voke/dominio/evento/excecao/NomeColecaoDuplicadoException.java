package br.voke.dominio.evento.excecao;

public class NomeColecaoDuplicadoException extends RuntimeException {
    public NomeColecaoDuplicadoException(String nome) {
        super("Já existe uma coleção com o nome \"" + nome + "\"");
    }
}
