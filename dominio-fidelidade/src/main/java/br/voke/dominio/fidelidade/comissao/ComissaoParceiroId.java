package br.voke.dominio.fidelidade.comissao;

import java.util.Objects;
import java.util.UUID;

public final class ComissaoParceiroId {
    private final UUID valor;

    public ComissaoParceiroId(UUID valor) {
        Objects.requireNonNull(valor, "Id da comissão é obrigatório");
        this.valor = valor;
    }

    public static ComissaoParceiroId novo() { return new ComissaoParceiroId(UUID.randomUUID()); }
    public static ComissaoParceiroId de(String valor) { return new ComissaoParceiroId(UUID.fromString(valor)); }
    public UUID getValor() { return valor; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComissaoParceiroId)) return false;
        return valor.equals(((ComissaoParceiroId) o).valor);
    }
    @Override public int hashCode() { return Objects.hash(valor); }
    @Override public String toString() { return valor.toString(); }
}
