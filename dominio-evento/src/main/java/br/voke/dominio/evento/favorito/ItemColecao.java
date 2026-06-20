package br.voke.dominio.evento.favorito;

import java.util.Objects;
import java.util.UUID;

public final class ItemColecao {
    private final UUID eventoId;
    private final int ordem;

    public ItemColecao(UUID eventoId, int ordem) {
        this.eventoId = Objects.requireNonNull(eventoId);
        this.ordem = ordem;
    }

    public UUID getEventoId() { return eventoId; }
    public int getOrdem() { return ordem; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemColecao that)) return false;
        return Objects.equals(eventoId, that.eventoId);
    }

    @Override
    public int hashCode() { return Objects.hash(eventoId); }
}
