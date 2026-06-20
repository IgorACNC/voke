package br.voke.dominio.fidelidade.excecao;

public class LimiteFrequenciaSaqueException extends RuntimeException {
    public LimiteFrequenciaSaqueException() {
        super("Limite de saques diários atingido");
    }
}
