package br.voke.dominio.pessoa.excecao;

public class DataNascimentoImutavelException extends RuntimeException {
    public DataNascimentoImutavelException() {
        super("Data de nascimento não pode ser alterada");
    }
}
