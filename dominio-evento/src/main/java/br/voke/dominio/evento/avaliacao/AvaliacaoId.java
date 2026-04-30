package br.voke.dominio.evento.avaliacao;

import java.util.Objects;
import java.util.UUID;

public final class AvaliacaoId {

    private final UUID valor;

    public AvaliacaoId(UUID valor) {
        Objects.requireNonNull(valor, "Id da avaliação é obrigatório");
        this.valor = valor;
    }

    public static AvaliacaoId novo() {
        return new AvaliacaoId(UUID.randomUUID());
    }

    public UUID getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AvaliacaoId)) return false;
        return valor.equals(((AvaliacaoId) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor.toString(); }
}
