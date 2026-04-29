package br.voke.dominio.evento.favorito;

import java.util.Objects;
import java.util.UUID;

public final class FavoritoId {
    private final UUID valor;

    public FavoritoId(UUID valor) {
        Objects.requireNonNull(valor, "Id do favorito é obrigatório");
        this.valor = valor;
    }

    public static FavoritoId novo() { return new FavoritoId(UUID.randomUUID()); }
    public UUID getValor() { return valor; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FavoritoId)) return false;
        return valor.equals(((FavoritoId) o).valor);
    }
    @Override public int hashCode() { return Objects.hash(valor); }
    @Override public String toString() { return valor.toString(); }
}