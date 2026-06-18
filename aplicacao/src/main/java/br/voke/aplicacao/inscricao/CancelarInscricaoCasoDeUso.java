package br.voke.aplicacao.inscricao;

import br.voke.aplicacao.evento.AtualizadorEstatisticaListener;
import br.voke.dominio.inscricao.inscricao.Inscricao;
import br.voke.dominio.inscricao.inscricao.InscricaoId;
import br.voke.dominio.inscricao.inscricao.InscricaoRepositorio;
import br.voke.dominio.inscricao.inscricao.InscricaoServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class CancelarInscricaoCasoDeUso {

    private final InscricaoServico servico;
    private final InscricaoRepositorio inscricaoRepositorio;
    private final AtualizadorEstatisticaListener atualizadorEstatistica;

    public CancelarInscricaoCasoDeUso(InscricaoServico servico,
                                      InscricaoRepositorio inscricaoRepositorio,
                                      AtualizadorEstatisticaListener atualizadorEstatistica) {
        Objects.requireNonNull(servico);
        Objects.requireNonNull(inscricaoRepositorio);
        Objects.requireNonNull(atualizadorEstatistica);
        this.servico = servico;
        this.inscricaoRepositorio = inscricaoRepositorio;
        this.atualizadorEstatistica = atualizadorEstatistica;
    }

    public BigDecimal executar(UUID inscricaoId, LocalDateTime dataEvento) {
        InscricaoId id = new InscricaoId(inscricaoId);
        Inscricao inscricao = inscricaoRepositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Inscricao nao encontrada"));
        UUID eventoId = inscricao.getEventoId();
        BigDecimal valorOriginal = inscricao.getValorPago();
        BigDecimal devolucao = servico.cancelar(id, dataEvento);
        // RN02: reverte a receita pelo valor pago original (nao pela devolucao parcial).
        atualizadorEstatistica.aoCancelarInscricao(eventoId, valorOriginal);
        return devolucao;
    }
}
