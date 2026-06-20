package br.voke.aplicacao.evento;

import br.voke.dominio.evento.avaliacao.Avaliacao;
import br.voke.dominio.evento.avaliacao.AvaliacaoServico;
import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.evento.StatusEvento;
import br.voke.dominio.inscricao.inscricao.Inscricao;
import br.voke.dominio.inscricao.inscricao.InscricaoRepositorio;
import br.voke.dominio.inscricao.inscricao.StatusInscricao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AvaliarEventoCasoDeUso {

    private final AvaliacaoServico servico;
    private final EventoRepositorio eventoRepositorio;
    private final InscricaoRepositorio inscricaoRepositorio;

    public AvaliarEventoCasoDeUso(AvaliacaoServico servico,
                                  EventoRepositorio eventoRepositorio,
                                  InscricaoRepositorio inscricaoRepositorio) {
        Objects.requireNonNull(servico);
        Objects.requireNonNull(eventoRepositorio);
        Objects.requireNonNull(inscricaoRepositorio);
        this.servico = servico;
        this.eventoRepositorio = eventoRepositorio;
        this.inscricaoRepositorio = inscricaoRepositorio;
    }

    public Avaliacao executar(UUID participanteId, UUID eventoId, int nota, String comentario) {
        Evento evento = eventoRepositorio.buscarPorId(new EventoId(eventoId))
                .orElseThrow(() -> new IllegalArgumentException("Evento nao encontrado"));

        boolean eventoFinalizado = evento.getStatus() == StatusEvento.ENCERRADO
                || evento.getDataHoraFim().isBefore(LocalDateTime.now());
        boolean inscricaoConfirmada = possuiInscricaoConfirmada(participanteId, eventoId);

        return servico.avaliar(participanteId, eventoId, nota, comentario, eventoFinalizado, inscricaoConfirmada);
    }

    public List<Evento> listarEventosAvaliaveis(UUID participanteId) {
        return inscricaoRepositorio.buscarPorParticipanteId(participanteId).stream()
                .filter(this::inscricaoPermiteAvaliacao)
                .map(i -> eventoRepositorio.buscarPorId(new EventoId(i.getEventoId())))
                .flatMap(OptionalUtils::stream)
                .filter(e -> e.getStatus() == StatusEvento.ENCERRADO
                        || e.getDataHoraFim().isBefore(LocalDateTime.now()))
                .toList();
    }

    private boolean possuiInscricaoConfirmada(UUID participanteId, UUID eventoId) {
        return inscricaoRepositorio.buscarPorParticipanteId(participanteId).stream()
                .anyMatch(i -> i.getEventoId().equals(eventoId) && inscricaoPermiteAvaliacao(i));
    }

    private boolean inscricaoPermiteAvaliacao(Inscricao inscricao) {
        return inscricao.getStatus() == StatusInscricao.CONFIRMADA
                || inscricao.getStatus() == StatusInscricao.CHECK_IN_REALIZADO;
    }

    private static final class OptionalUtils {
        private OptionalUtils() {}

        static <T> java.util.stream.Stream<T> stream(java.util.Optional<T> optional) {
            return optional.map(java.util.stream.Stream::of).orElseGet(java.util.stream.Stream::empty);
        }
    }
}
