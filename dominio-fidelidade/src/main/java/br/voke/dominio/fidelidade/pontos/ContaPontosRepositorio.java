package br.voke.dominio.fidelidade.pontos;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContaPontosRepositorio {
    void salvar(ContaPontos conta);
    Optional<ContaPontos> buscarPorId(ContaPontosId id);
    Optional<ContaPontos> buscarPorParticipanteId(UUID participanteId);
    /** RN4 - varredura para o CRON de expiração. */
    List<ContaPontos> listarTodas();
}