package br.voke.dominio.fidelidade.recompensa;

import java.util.UUID;

public interface RecompensaObserver {

    default void onRecompensaResgatada(Recompensa recompensa, UUID participanteId) {
    }

    default void onRecompensaEsgotada(Recompensa recompensa) {
    }
}
