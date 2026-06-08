package br.voke.dominio.fidelidade.sugestao;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MotorSugestoes {

    private final EventoConsultaGateway eventoGateway;
    private final InscricaoConsultaGateway inscricaoGateway;

    public MotorSugestoes(EventoConsultaGateway eventoGateway, InscricaoConsultaGateway inscricaoGateway) {
        Objects.requireNonNull(eventoGateway, "EventoConsultaGateway é obrigatório");
        Objects.requireNonNull(inscricaoGateway, "InscricaoConsultaGateway é obrigatório");
        this.eventoGateway = eventoGateway;
        this.inscricaoGateway = inscricaoGateway;
    }

    public List<UUID> recomendar(PreferenciaParticipante preferencia, int limite) {
        Objects.requireNonNull(preferencia, "Preferência é obrigatória");
        if (preferencia.getCategoriasPreferidas().isEmpty()) {
            return List.of();
        }
        UUID participanteId = preferencia.getParticipanteId();

        List<EventoConsultaGateway.EventoCandidato> candidatos =
                eventoGateway.buscarEventosCandidatosPorTags(preferencia.getCategoriasPreferidas());

        return candidatos.stream()
                .filter(c -> eventoGateway.eventoEstaDisponivel(c.eventoId()))
                .filter(c -> !inscricaoGateway.participanteJaInscritoOuAguardando(participanteId, c.eventoId()))
                .filter(c -> !preferencia.deveDescartar(c.tags()))
                .sorted(Comparator.comparingInt(
                        (EventoConsultaGateway.EventoCandidato c) -> preferencia.calcularScore(c.tags())).reversed())
                .limit(limite)
                .map(EventoConsultaGateway.EventoCandidato::eventoId)
                .toList();
    }
}
