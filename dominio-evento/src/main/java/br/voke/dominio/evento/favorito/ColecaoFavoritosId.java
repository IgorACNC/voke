package br.voke.dominio.evento.favorito;

import java.util.Objects;
import java.util.UUID;

public final class ColecaoFavoritosId {
    private final UUID valor;

    public ColecaoFavoritosId(UUID valor) {
        this.valor = Objects.requireNonNull(valor);
    }

    public static ColecaoFavoritosId novo() {
        return new ColecaoFavoritosId(UUID.randomUUID());
    }

    public UUID getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ColecaoFavoritosId that)) return false;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor.toString(); }
}
