package br.voke.dominio.pessoa.admin;

import java.util.Objects;
import java.util.UUID;

public final class AdministradorId {

    private final UUID valor;

    public AdministradorId(UUID valor) {
        Objects.requireNonNull(valor, "Id do administrador é obrigatório");
        this.valor = valor;
    }

    public static AdministradorId novo() { return new AdministradorId(UUID.randomUUID()); }
    public UUID getValor() { return valor; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdministradorId)) return false;
        return valor.equals(((AdministradorId) o).valor);
    }
    @Override public int hashCode() { return Objects.hash(valor); }
    @Override public String toString() { return valor.toString(); }
}
