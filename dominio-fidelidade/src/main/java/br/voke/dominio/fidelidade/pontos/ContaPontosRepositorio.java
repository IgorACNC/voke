package br.voke.dominio.fidelidade.pontos;

import java.util.Optional;
import java.util.UUID;

public interface ContaPontosRepositorio {
    void salvar(ContaPontos conta);
    Optional<ContaPontos> buscarPorId(ContaPontosId id);
    Optional<ContaPontos> buscarPorParticipanteId(UUID participanteId);
}