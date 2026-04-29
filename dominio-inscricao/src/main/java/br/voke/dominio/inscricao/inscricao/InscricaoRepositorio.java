package br.voke.dominio.inscricao.inscricao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InscricaoRepositorio {
    void salvar(Inscricao inscricao);
    Optional<Inscricao> buscarPorId(InscricaoId id);
    List<Inscricao> buscarPorParticipanteId(UUID participanteId);
    void remover(InscricaoId id);
    int contarPorParticipanteEEvento(UUID participanteId, UUID eventoId);
    boolean existeConflitoDeHorario(UUID participanteId, LocalDateTime inicio, LocalDateTime fim);
}
