package br.voke.aplicacao.inscricao;

import br.voke.aplicacao.fidelidade.CreditarPontosCasoDeUso;
import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.fidelidade.pontos.GanhoPontosRegular;
import br.voke.dominio.inscricao.inscricao.Inscricao;
import br.voke.dominio.inscricao.inscricao.InscricaoId;
import br.voke.dominio.inscricao.inscricao.InscricaoRepositorio;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class RealizarCheckInCasoDeUso {

    private final InscricaoRepositorio inscricaoRepositorio;
    private final EventoRepositorio eventoRepositorio;
    private final CreditarPontosCasoDeUso creditarPontos;

    public RealizarCheckInCasoDeUso(InscricaoRepositorio inscricaoRepositorio,
                                     EventoRepositorio eventoRepositorio,
                                     CreditarPontosCasoDeUso creditarPontos) {
        Objects.requireNonNull(inscricaoRepositorio);
        Objects.requireNonNull(eventoRepositorio);
        Objects.requireNonNull(creditarPontos);
        this.inscricaoRepositorio = inscricaoRepositorio;
        this.eventoRepositorio = eventoRepositorio;
        this.creditarPontos = creditarPontos;
    }

    public Resultado executar(UUID inscricaoId) {
        Inscricao inscricao = inscricaoRepositorio.buscarPorId(new InscricaoId(inscricaoId))
                .orElseThrow(() -> new IllegalArgumentException("Inscrição não encontrada"));

        Evento evento = eventoRepositorio.buscarPorId(new EventoId(inscricao.getEventoId()))
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));

        if (!inscricao.fezCheckIn()) {
            inscricao.realizarCheckIn();
        }

        boolean eventoEncerrado = evento.getDataHoraFim().isBefore(LocalDateTime.now());
        int pontosGanhos = 0;
        if (eventoEncerrado && !inscricao.pontosJaCreditados()) {
            // RN1: gatilho de validação dupla — evento encerrado E check-in realizado.
            // Regra: 30% do valor pago vira pontos (arredondado pra cima).
            int pontosBase = inscricao.getValorPago()
                    .multiply(new java.math.BigDecimal("0.30"))
                    .setScale(0, java.math.RoundingMode.CEILING)
                    .intValue();
            if (pontosBase > 0) {
                creditarPontos.executarComReferencia(inscricao.getParticipanteId(), pontosBase,
                        true, true, new GanhoPontosRegular(),
                        inscricao.getEventoId(), "Presença em " + evento.getNome());
                pontosGanhos = pontosBase;
                inscricao.marcarPontosCreditados();
            }
        }

        inscricaoRepositorio.salvar(inscricao);
        return new Resultado(eventoEncerrado, pontosGanhos);
    }

    public record Resultado(boolean pontosCreditados, int pontos) {}
}
