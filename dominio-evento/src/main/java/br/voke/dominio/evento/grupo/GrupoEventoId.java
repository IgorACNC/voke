package br.voke.dominio.evento.grupo;

import java.util.Objects;
import java.util.UUID;

public final class GrupoEventoId {

    private final UUID valor;

    public GrupoEventoId(UUID valor) {
        Objects.requireNonNull(valor, "Id do grupo é obrigatório");
        this.valor = valor;
    }

    public static GrupoEventoId novo() {
        return new GrupoEventoId(UUID.randomUUID());
    }

    public UUID getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GrupoEventoId)) return false;
        return valor.equals(((GrupoEventoId) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor.toString(); }
}
