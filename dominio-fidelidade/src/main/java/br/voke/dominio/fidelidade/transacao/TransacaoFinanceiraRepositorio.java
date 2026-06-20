package br.voke.dominio.fidelidade.transacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransacaoFinanceiraRepositorio {
    void salvar(TransacaoFinanceira transacao);
    List<TransacaoFinanceira> buscarPorParticipanteId(UUID participanteId);

    /** RN1 - soma de DEPOSITOS feitos por um participante a partir do instante informado. */
    BigDecimal somarDepositosDesde(UUID participanteId, LocalDateTime desde);

    /** RN2 - quantidade de SAQUES feitos por um participante a partir do instante informado. */
    int contarSaquesDesde(UUID participanteId, LocalDateTime desde);
}
