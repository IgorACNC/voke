package br.voke.dominio.inscricao.convite;

import java.util.Objects;
import java.util.UUID;

public class ConviteId {
    private final UUID valor;

    public ConviteId(UUID valor) {
        this.valor = Objects.requireNonNull(valor, "ID do convite é obrigatório");
    }

    public static ConviteId novo() {
        return new ConviteId(UUID.randomUUID());
    }

    public UUID getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConviteId)) return false;
        return valor.equals(((ConviteId) o).valor);
    }

    @Override
    public int hashCode() { return valor.hashCode(); }
}
