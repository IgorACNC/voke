package br.voke.infraestrutura.fidelidade.sugestao;

import br.voke.dominio.fidelidade.sugestao.InscricaoConsultaGateway;
import br.voke.dominio.inscricao.inscricao.Inscricao;
import br.voke.dominio.inscricao.inscricao.InscricaoRepositorio;
import br.voke.dominio.inscricao.inscricao.StatusInscricao;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class InscricaoConsultaGatewayAdapter implements InscricaoConsultaGateway {

    private final InscricaoRepositorio inscricaoRepositorio;

    public InscricaoConsultaGatewayAdapter(InscricaoRepositorio inscricaoRepositorio) {
        this.inscricaoRepositorio = inscricaoRepositorio;
    }

    @Override
    public boolean participanteJaInscritoOuAguardando(UUID participanteId, UUID eventoId) {
        return inscricaoRepositorio.buscarPorParticipanteId(participanteId).stream()
                .anyMatch(i -> i.getEventoId().equals(eventoId)
                        && i.getStatus() != StatusInscricao.CANCELADA);
    }

    @Override
    public Set<UUID> buscarHistoricoEventosDoParticipante(UUID participanteId) {
        return inscricaoRepositorio.buscarPorParticipanteId(participanteId).stream()
                .map(Inscricao::getEventoId)
                .collect(Collectors.toSet());
    }
}
