package br.voke.dominio.fidelidade.pontos;

import java.util.Objects;
import java.util.UUID;

public final class ContaPontosId {
    private final UUID valor;

    public ContaPontosId(UUID valor) {
        Objects.requireNonNull(valor, "Id da conta de pontos é obrigatório");
        this.valor = valor;
    }

    public static ContaPontosId novo() { return new ContaPontosId(UUID.randomUUID()); }
    public UUID getValor() { return valor; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContaPontosId)) return false;
        return valor.equals(((ContaPontosId) o).valor);
    }
    @Override public int hashCode() { return Objects.hash(valor); }
    @Override public String toString() { return valor.toString(); }
}