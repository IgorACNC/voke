package br.voke.dominio.pessoa.amizade;

import java.util.Objects;
import java.util.UUID;

public final class ComunidadeAmigosId {

    private final UUID valor;

    public ComunidadeAmigosId(UUID valor) {
        Objects.requireNonNull(valor, "Id da comunidade é obrigatório");
        this.valor = valor;
    }

    public static ComunidadeAmigosId novo() {
        return new ComunidadeAmigosId(UUID.randomUUID());
    }

    public UUID getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComunidadeAmigosId)) return false;
        return valor.equals(((ComunidadeAmigosId) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor.toString(); }
}
