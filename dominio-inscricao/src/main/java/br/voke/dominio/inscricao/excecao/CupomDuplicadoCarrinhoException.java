package br.voke.dominio.inscricao.excecao;

public class CupomDuplicadoCarrinhoException extends RuntimeException {
    public CupomDuplicadoCarrinhoException() {
        super("Apenas um cupom pode ser utilizado por compra");
    }
}