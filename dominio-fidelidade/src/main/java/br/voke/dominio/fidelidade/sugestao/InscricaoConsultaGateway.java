package br.voke.dominio.fidelidade.sugestao;

import java.util.Set;
import java.util.UUID;

public interface InscricaoConsultaGateway {
    boolean participanteJaInscritoOuAguardando(UUID participanteId, UUID eventoId);

    /**
     * Retorna os ids dos eventos em que o participante já se inscreveu
     * (incluindo inscrições canceladas — representam interesse passado).
     */
    Set<UUID> buscarHistoricoEventosDoParticipante(UUID participanteId);
}
