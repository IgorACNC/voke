package br.voke.dominio.fidelidade.pontos;

import java.util.Objects;
import java.util.UUID;

public final class TransacaoPontosId {

    private final UUID valor;

    public TransacaoPontosId(UUID valor) {
        Objects.requireNonNull(valor, "Id da transação é obrigatório");
        this.valor = valor;
    }

    public static TransacaoPontosId novo() {
        return new TransacaoPontosId(UUID.randomUUID());
    }

    public UUID getValor() { return valor; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransacaoPontosId other)) return false;
        return valor.equals(other.valor);
    }
    @Override public int hashCode() { return Objects.hash(valor); }
    @Override public String toString() { return valor.toString(); }
}
