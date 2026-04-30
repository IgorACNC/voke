package br.voke.dominio.fidelidade.recompensa;

import java.util.Objects;
import java.util.UUID;

public final class RecompensaId {
    private final UUID valor;

    public RecompensaId(UUID valor) {
        Objects.requireNonNull(valor, "Id da recompensa é obrigatório");
        this.valor = valor;
    }

    public static RecompensaId novo() { return new RecompensaId(UUID.randomUUID()); }
    public UUID getValor() { return valor; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecompensaId)) return false;
        return valor.equals(((RecompensaId) o).valor);
    }
    @Override public int hashCode() { return Objects.hash(valor); }
    @Override public String toString() { return valor.toString(); }
}
