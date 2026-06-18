package br.voke.dominio.evento.estatistica;

import java.util.Objects;
import java.util.UUID;

public final class EstatisticaEventoId {

    private final UUID valor;

    public EstatisticaEventoId(UUID valor) {
        Objects.requireNonNull(valor, "Id da estatistica e obrigatorio");
        this.valor = valor;
    }

    public static EstatisticaEventoId novo() {
        return new EstatisticaEventoId(UUID.randomUUID());
    }

    public UUID getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EstatisticaEventoId)) return false;
        return valor.equals(((EstatisticaEventoId) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor.toString(); }
}
