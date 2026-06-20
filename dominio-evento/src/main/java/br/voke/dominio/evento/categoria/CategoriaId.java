package br.voke.dominio.evento.categoria;

import java.util.Objects;
import java.util.UUID;

public final class CategoriaId {

    private final UUID valor;

    public CategoriaId(UUID valor) {
        Objects.requireNonNull(valor, "Id da categoria é obrigatório");
        this.valor = valor;
    }

    public static CategoriaId novo() { return new CategoriaId(UUID.randomUUID()); }
    public UUID getValor() { return valor; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoriaId)) return false;
        return valor.equals(((CategoriaId) o).valor);
    }
    @Override public int hashCode() { return Objects.hash(valor); }
    @Override public String toString() { return valor.toString(); }
}
