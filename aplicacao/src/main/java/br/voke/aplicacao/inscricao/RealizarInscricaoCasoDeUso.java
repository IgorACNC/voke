package br.voke.aplicacao.inscricao;

import br.voke.aplicacao.evento.AtualizadorEstatisticaListener;
import br.voke.dominio.inscricao.inscricao.Inscricao;
import br.voke.dominio.inscricao.inscricao.InscricaoServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class RealizarInscricaoCasoDeUso {

    private final InscricaoServico servico;
    private final AtualizadorEstatisticaListener atualizadorEstatistica;

    public RealizarInscricaoCasoDeUso(InscricaoServico servico,
                                      AtualizadorEstatisticaListener atualizadorEstatistica) {
        Objects.requireNonNull(servico);
        Objects.requireNonNull(atualizadorEstatistica);
        this.servico = servico;
        this.atualizadorEstatistica = atualizadorEstatistica;
    }

    public Inscricao executar(UUID participanteId, UUID eventoId, BigDecimal valorIngresso,
                              int idadeParticipante, int idadeMinimaEvento,
                              boolean eventoAtivo, boolean possuiVagas,
                              LocalDateTime eventoInicio, LocalDateTime eventoFim,
                              int limitePorCpf) {
        Inscricao inscricao = servico.realizar(participanteId, eventoId, valorIngresso,
                idadeParticipante, idadeMinimaEvento, eventoAtivo, possuiVagas,
                eventoInicio, eventoFim, limitePorCpf);
        atualizadorEstatistica.aoConfirmarInscricao(eventoId, inscricao.getValorPago());
        return inscricao;
    }
}
