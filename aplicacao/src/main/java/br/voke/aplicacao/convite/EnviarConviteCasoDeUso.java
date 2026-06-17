package br.voke.aplicacao.convite;

import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.evento.StatusEvento;
import br.voke.dominio.inscricao.convite.Convite;
import br.voke.dominio.inscricao.convite.ConviteServico;
import br.voke.dominio.inscricao.inscricao.InscricaoRepositorio;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;

import java.util.Objects;
import java.util.UUID;

public class EnviarConviteCasoDeUso {

    private final ConviteServico servico;
    private final EventoRepositorio eventoRepositorio;
    private final ParticipanteRepositorio participanteRepositorio;
    private final InscricaoRepositorio inscricaoRepositorio;

    public EnviarConviteCasoDeUso(ConviteServico servico, EventoRepositorio eventoRepositorio,
                                   ParticipanteRepositorio participanteRepositorio,
                                   InscricaoRepositorio inscricaoRepositorio) {
        Objects.requireNonNull(servico);
        Objects.requireNonNull(eventoRepositorio);
        Objects.requireNonNull(participanteRepositorio);
        Objects.requireNonNull(inscricaoRepositorio);
        this.servico = servico;
        this.eventoRepositorio = eventoRepositorio;
        this.participanteRepositorio = participanteRepositorio;
        this.inscricaoRepositorio = inscricaoRepositorio;
    }

    public Convite executar(UUID remetenteId, String emailDestinatario, UUID eventoId) {
        Participante destinatario = participanteRepositorio.buscarPorEmail(new Email(emailDestinatario))
                .orElseThrow(() -> new IllegalArgumentException("Participante destinatário não encontrado"));

        Evento evento = eventoRepositorio.buscarPorId(new EventoId(eventoId))
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));

        boolean eventoAtivo = evento.getStatus() == StatusEvento.ATIVO
                || evento.getStatus() == StatusEvento.PUBLICADO;

        boolean jaInscrito = inscricaoRepositorio.contarPorParticipanteEEvento(
                destinatario.getId().getValor(), eventoId) > 0;

        return servico.enviar(remetenteId, destinatario.getId().getValor(), eventoId, eventoAtivo, jaInscrito);
    }
}
