package br.voke.aplicacao.inscricao;

import br.voke.dominio.inscricao.inscricao.Inscricao;
import br.voke.dominio.inscricao.inscricao.InscricaoServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class RealizarInscricaoCasoDeUso {

    private final InscricaoServico servico;

    public RealizarInscricaoCasoDeUso(InscricaoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public Inscricao executar(UUID participanteId, UUID eventoId, BigDecimal valorIngresso,
                              int idadeParticipante, int idadeMinimaEvento,
                              boolean eventoAtivo, boolean possuiVagas,
                              LocalDateTime eventoInicio, LocalDateTime eventoFim,
                              int limitePorCpf) {
        return servico.realizar(participanteId, eventoId, valorIngresso,
                idadeParticipante, idadeMinimaEvento, eventoAtivo, possuiVagas,
                eventoInicio, eventoFim, limitePorCpf);
    }
}
