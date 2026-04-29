package br.voke.dominio.inscricao.excecao;

public class LimiteEventosCarrinhoException extends RuntimeException {
    public LimiteEventosCarrinhoException() {
        super("Limite de 2 eventos por carrinho atingido. Finalize a compra atual para continuar");
    }
}