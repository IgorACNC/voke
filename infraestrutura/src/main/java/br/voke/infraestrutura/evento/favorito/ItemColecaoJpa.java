package br.voke.infraestrutura.evento.favorito;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class ItemColecaoJpa {

    @Column(name = "evento_id", nullable = false)
    private UUID eventoId;

    @Column(nullable = false)
    private int ordem;

    protected ItemColecaoJpa() {}

    public ItemColecaoJpa(UUID eventoId, int ordem) {
        this.eventoId = eventoId;
        this.ordem = ordem;
    }

    public UUID getEventoId() { return eventoId; }
    public int getOrdem() { return ordem; }
}
