package br.voke.dominio.pessoa.participante;

import java.util.Objects;
import java.util.UUID;

public final class ParticipanteId {

    private final UUID valor;

    public ParticipanteId(UUID valor) {
        Objects.requireNonNull(valor, "Id do participante é obrigatório");
        this.valor = valor;
    }

    public static ParticipanteId novo() {
        return new ParticipanteId(UUID.randomUUID());
    }

    public UUID getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticipanteId)) return false;
        return valor.equals(((ParticipanteId) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor.toString(); }
}
