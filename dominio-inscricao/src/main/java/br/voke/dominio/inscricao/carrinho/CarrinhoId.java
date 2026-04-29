package br.voke.dominio.inscricao.carrinho;

import java.util.Objects;
import java.util.UUID;

public final class CarrinhoId {
    private final UUID valor;

    public CarrinhoId(UUID valor) {
        Objects.requireNonNull(valor, "Id do carrinho é obrigatório");
        this.valor = valor;
    }

    public static CarrinhoId novo() { return new CarrinhoId(UUID.randomUUID()); }
    public UUID getValor() { return valor; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CarrinhoId)) return false;
        return valor.equals(((CarrinhoId) o).valor);
    }
    @Override public int hashCode() { return Objects.hash(valor); }
    @Override public String toString() { return valor.toString(); }
}