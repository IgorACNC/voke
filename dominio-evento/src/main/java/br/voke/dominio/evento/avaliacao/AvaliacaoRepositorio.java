package br.voke.dominio.evento.avaliacao;

import java.util.Optional;
import java.util.UUID;

public interface AvaliacaoRepositorio {
    void salvar(Avaliacao avaliacao);
    Optional<Avaliacao> buscarPorId(AvaliacaoId id);
    Optional<Avaliacao> buscarPorParticipanteEEvento(UUID participanteId, UUID eventoId);
    void remover(AvaliacaoId id);
    boolean existePorParticipanteEEvento(UUID participanteId, UUID eventoId);
}
