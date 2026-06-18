package br.voke.dominio.fidelidade.pontos;

import java.util.List;
import java.util.UUID;

public interface TransacaoPontosRepositorio {
    void salvar(TransacaoPontos transacao);
    List<TransacaoPontos> buscarPorParticipanteId(UUID participanteId);
}
