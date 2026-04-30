package br.voke.dominio.pessoa.excecao;

public class AcessoRestritoPorIdadeException extends RuntimeException {
    public AcessoRestritoPorIdadeException() {
        super("Funcionalidade disponível apenas para maiores de 16 anos");
    }
}
