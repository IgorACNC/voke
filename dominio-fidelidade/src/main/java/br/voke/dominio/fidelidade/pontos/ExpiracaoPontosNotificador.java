package br.voke.dominio.fidelidade.pontos;

import java.util.UUID;

public interface ExpiracaoPontosNotificador {
    void notificarExpiracao(UUID participanteId, int pontosExpirados);
}