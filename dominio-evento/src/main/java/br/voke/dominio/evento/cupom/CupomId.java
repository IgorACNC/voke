package br.voke.dominio.evento.cupom;

import java.util.Objects;
import java.util.UUID;

public final class CupomId {
    private final UUID valor;

    public CupomId(UUID valor) {
        Objects.requireNonNull(valor, "Id do cupom é obrigatório");
        this.valor = valor;
    }

    public static CupomId novo() { return new CupomId(UUID.randomUUID()); }
    public UUID getValor() { return valor; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CupomId)) return false;
        return valor.equals(((CupomId) o).valor);
    }
    @Override public int hashCode() { return Objects.hash(valor); }
    @Override public String toString() { return valor.toString(); }
}
