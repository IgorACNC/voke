package br.voke.dominio.evento.faq;

import java.util.Objects;
import java.util.UUID;

public final class PerguntaFrequenteId {
    private final UUID valor;

    public PerguntaFrequenteId(UUID valor) {
        this.valor = Objects.requireNonNull(valor);
    }

    public static PerguntaFrequenteId novo() {
        return new PerguntaFrequenteId(UUID.randomUUID());
    }

    public UUID getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PerguntaFrequenteId that)) return false;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor.toString(); }
}
