package br.voke.dominio.evento.notificacao;

import java.util.Objects;
import java.util.UUID;

public final class NotificacaoId {
    private final UUID valor;

    public NotificacaoId(UUID valor) {
        Objects.requireNonNull(valor, "Id da notificação é obrigatório");
        this.valor = valor;
    }

    public static NotificacaoId novo() { return new NotificacaoId(UUID.randomUUID()); }
    public UUID getValor() { return valor; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificacaoId)) return false;
        return valor.equals(((NotificacaoId) o).valor);
    }
    @Override public int hashCode() { return Objects.hash(valor); }
    @Override public String toString() { return valor.toString(); }
}
