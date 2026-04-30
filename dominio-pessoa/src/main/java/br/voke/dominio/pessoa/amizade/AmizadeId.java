package br.voke.dominio.pessoa.amizade;

import java.util.Objects;
import java.util.UUID;

public final class AmizadeId {

    private final UUID valor;

    public AmizadeId(UUID valor) {
        Objects.requireNonNull(valor, "Id da amizade é obrigatório");
        this.valor = valor;
    }

    public static AmizadeId novo() {
        return new AmizadeId(UUID.randomUUID());
    }

    public UUID getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AmizadeId)) return false;
        return valor.equals(((AmizadeId) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor.toString(); }
}
