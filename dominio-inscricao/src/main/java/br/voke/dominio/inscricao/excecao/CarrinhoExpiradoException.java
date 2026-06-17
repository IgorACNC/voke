package br.voke.dominio.inscricao.excecao;

public class CarrinhoExpiradoException extends RuntimeException {
    public CarrinhoExpiradoException() {
        super("O tempo para finalizar a compra expirou. Adicione os itens novamente ao carrinho");
    }
}
