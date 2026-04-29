package br.voke.dominio.pessoa.parceiro;

import java.util.Objects;
import java.util.UUID;

public final class ParceiroId {

    private final UUID valor;

    public ParceiroId(UUID valor) {
        Objects.requireNonNull(valor, "Id do parceiro é obrigatório");
        this.valor = valor;
    }

    public static ParceiroId novo() {
        return new ParceiroId(UUID.randomUUID());
    }

    public UUID getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParceiroId)) return false;
        return valor.equals(((ParceiroId) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor.toString(); }
}
