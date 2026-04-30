package br.voke.dominio.fidelidade.excecao;

public class CongelamentoDePrecoException extends RuntimeException {
    public CongelamentoDePrecoException() {
        super("O valor da recompensa só pode ser alterado após 1 mês da última modificação");
    }
}
