package br.voke.dominio.fidelidade.sugestao;

import java.util.Objects;
import java.util.UUID;

public final class SugestaoId {
    private final UUID valor;

    public SugestaoId(UUID valor) {
        Objects.requireNonNull(valor, "Id da sugestão é obrigatório");
        this.valor = valor;
    }

    public static SugestaoId novo() { return new SugestaoId(UUID.randomUUID()); }
    public UUID getValor() { return valor; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SugestaoId)) return false;
        return valor.equals(((SugestaoId) o).valor);
    }
    @Override public int hashCode() { return Objects.hash(valor); }
    @Override public String toString() { return valor.toString(); }
}
