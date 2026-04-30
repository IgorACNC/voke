package br.voke.dominio.pessoa.excecao;

public class LimiteAtividadesException extends RuntimeException {
    public LimiteAtividadesException() {
        super("Limite de atividades do parceiro atingido");
    }
}
