package br.voke.dominio.fidelidade.transacao;

import java.util.List;
import java.util.UUID;

public interface TransacaoFinanceiraRepositorio {
    void salvar(TransacaoFinanceira transacao);
    List<TransacaoFinanceira> buscarPorParticipanteId(UUID participanteId);
}
