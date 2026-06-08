package br.voke.dominio.fidelidade.sugestao;

import java.util.Optional;
import java.util.UUID;

public interface PreferenciaParticipanteRepositorio {
    void salvar(PreferenciaParticipante preferencia);
    Optional<PreferenciaParticipante> buscarPorParticipanteId(UUID participanteId);
    void remover(PreferenciaParticipanteId id);
}
