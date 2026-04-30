package br.voke.dominio.pessoa.organizador;

import java.util.Objects;
import java.util.UUID;

public final class OrganizadorId {

    private final UUID valor;

    public OrganizadorId(UUID valor) {
        Objects.requireNonNull(valor, "Id do organizador é obrigatório");
        this.valor = valor;
    }

    public static OrganizadorId novo() {
        return new OrganizadorId(UUID.randomUUID());
    }

    public UUID getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrganizadorId)) return false;
        return valor.equals(((OrganizadorId) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor.toString(); }
}
