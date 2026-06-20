package br.voke.dominio.fidelidade.sugestao;

import java.util.Set;
import java.util.UUID;

public interface FavoritoConsultaGateway {
    /**
     * Retorna os ids dos eventos que o participante adicionou aos favoritos.
     */
    Set<UUID> buscarFavoritosDoParticipante(UUID participanteId);
}
