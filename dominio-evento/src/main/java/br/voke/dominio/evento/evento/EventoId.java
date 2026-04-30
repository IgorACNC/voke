package br.voke.dominio.evento.evento;

import java.util.Objects;
import java.util.UUID;

public final class EventoId {

    private final UUID valor;

    public EventoId(UUID valor) {
        Objects.requireNonNull(valor, "Id do evento é obrigatório");
        this.valor = valor;
    }

    public static EventoId novo() {
        return new EventoId(UUID.randomUUID());
    }

    public UUID getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventoId)) return false;
        return valor.equals(((EventoId) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor.toString(); }
}
