package br.voke.dominio.fidelidade.recompensa;

import java.util.UUID;

public class LogRecompensaObserver implements RecompensaObserver {

    @Override
    public void onRecompensaResgatada(Recompensa recompensa, UUID participanteId) {
        System.out.printf(
                "[RESGATE] Participante %s resgatou a recompensa '%s' (id=%s). Estoque restante: %d%n",
                participanteId,
                recompensa.getNome(),
                recompensa.getId(),
                recompensa.getEstoqueRestante()
        );
    }

    @Override
    public void onRecompensaEsgotada(Recompensa recompensa) {
        System.out.printf(
                "[ESGOTADA] A recompensa '%s' (id=%s) do organizador %s esgotou! Estoque total: %d%n",
                recompensa.getNome(),
                recompensa.getId(),
                recompensa.getOrganizadorId(),
                recompensa.getEstoqueTotal()
        );
    }
}
