package br.voke.dominio.fidelidade.pontos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransacaoPontosRepositorio {
    void salvar(TransacaoPontos transacao);
    List<TransacaoPontos> buscarPorParticipanteId(UUID participanteId);

    /** RN4 - soma de transações de um tipo cuja dataHora seja anterior ao limite. */
    int somarPontosPorTipoAteData(UUID participanteId, TipoTransacaoPontos tipo, LocalDateTime ate);

    /** RN4 - soma total de transações de um tipo para o participante. */
    int somarPontosPorTipo(UUID participanteId, TipoTransacaoPontos tipo);
}
