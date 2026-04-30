package br.voke.dominio.fidelidade.carteira;

import java.util.Objects;
import java.util.UUID;

public final class CarteiraVirtualId {
    private final UUID valor;

    public CarteiraVirtualId(UUID valor) {
        Objects.requireNonNull(valor, "Id da carteira é obrigatório");
        this.valor = valor;
    }

    public static CarteiraVirtualId novo() { return new CarteiraVirtualId(UUID.randomUUID()); }
    public UUID getValor() { return valor; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CarteiraVirtualId)) return false;
        return valor.equals(((CarteiraVirtualId) o).valor);
    }
    @Override public int hashCode() { return Objects.hash(valor); }
    @Override public String toString() { return valor.toString(); }
}