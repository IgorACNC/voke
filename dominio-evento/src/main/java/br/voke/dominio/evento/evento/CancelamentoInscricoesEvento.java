package br.voke.dominio.evento.evento;

import java.util.UUID;

public interface CancelamentoInscricoesEvento {
    void cancelarInscricoesDoEvento(UUID eventoId);
}
