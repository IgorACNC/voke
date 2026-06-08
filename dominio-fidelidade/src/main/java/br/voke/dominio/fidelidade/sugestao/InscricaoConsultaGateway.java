package br.voke.dominio.fidelidade.sugestao;

import java.util.UUID;

public interface InscricaoConsultaGateway {
    boolean participanteJaInscritoOuAguardando(UUID participanteId, UUID eventoId);
}
