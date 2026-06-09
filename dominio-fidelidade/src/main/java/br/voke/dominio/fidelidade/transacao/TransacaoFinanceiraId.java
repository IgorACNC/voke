package br.voke.dominio.fidelidade.transacao;

import java.util.Objects;
import java.util.UUID;

public final class TransacaoFinanceiraId {

    private final UUID valor;

    public TransacaoFinanceiraId(UUID valor) {
        Objects.requireNonNull(valor, "Id da transação é obrigatório");
        this.valor = valor;
    }

    public static TransacaoFinanceiraId novo() {
        return new TransacaoFinanceiraId(UUID.randomUUID());
    }

    public UUID getValor() { return valor; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransacaoFinanceiraId)) return false;
        return valor.equals(((TransacaoFinanceiraId) o).valor);
    }
    @Override public int hashCode() { return Objects.hash(valor); }
    @Override public String toString() { return valor.toString(); }
}
