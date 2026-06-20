package br.voke.dominio.evento.subgrupo;

import java.util.Objects;
import java.util.UUID;

public final class SubgrupoId {

    private final UUID valor;

    public SubgrupoId(UUID valor) {
        Objects.requireNonNull(valor, "Id do subgrupo é obrigatório");
        this.valor = valor;
    }

    public static SubgrupoId novo() {
        return new SubgrupoId(UUID.randomUUID());
    }

    public UUID getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubgrupoId)) return false;
        return valor.equals(((SubgrupoId) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor.toString(); }
}
