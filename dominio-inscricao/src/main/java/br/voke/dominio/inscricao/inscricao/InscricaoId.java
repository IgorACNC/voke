package br.voke.dominio.inscricao.inscricao;

import java.util.Objects;
import java.util.UUID;

public final class InscricaoId {
    private final UUID valor;

    public InscricaoId(UUID valor) {
        Objects.requireNonNull(valor, "Id da inscrição é obrigatório");
        this.valor = valor;
    }

    public static InscricaoId novo() { return new InscricaoId(UUID.randomUUID()); }
    public UUID getValor() { return valor; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InscricaoId)) return false;
        return valor.equals(((InscricaoId) o).valor);
    }
    @Override public int hashCode() { return Objects.hash(valor); }
    @Override public String toString() { return valor.toString(); }
}
