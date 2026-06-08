package br.voke.dominio.fidelidade.sugestao;

import java.util.Objects;
import java.util.UUID;

public final class PreferenciaParticipanteId {
    private final UUID valor;

    public PreferenciaParticipanteId(UUID valor) {
        Objects.requireNonNull(valor, "Id da preferência é obrigatório");
        this.valor = valor;
    }

    public static PreferenciaParticipanteId novo() { return new PreferenciaParticipanteId(UUID.randomUUID()); }
    public UUID getValor() { return valor; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PreferenciaParticipanteId)) return false;
        return valor.equals(((PreferenciaParticipanteId) o).valor);
    }
    @Override public int hashCode() { return Objects.hash(valor); }
    @Override public String toString() { return valor.toString(); }
}
