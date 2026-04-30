package br.voke.dominio.fidelidade.sugestao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SugestaoRepositorio {
    void salvar(Sugestao sugestao);
    Optional<Sugestao> buscarPorId(SugestaoId id);
    List<Sugestao> buscarPorParticipanteId(UUID participanteId);
    void remover(SugestaoId id);
    int contarSugestoesSemanalPorParticipante(UUID participanteId);
}
