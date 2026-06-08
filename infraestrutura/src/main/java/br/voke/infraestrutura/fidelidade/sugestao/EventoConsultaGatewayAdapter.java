package br.voke.infraestrutura.fidelidade.sugestao;

import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.fidelidade.sugestao.EventoConsultaGateway;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class EventoConsultaGatewayAdapter implements EventoConsultaGateway {

    private final EventoRepositorio eventoRepositorio;

    public EventoConsultaGatewayAdapter(EventoRepositorio eventoRepositorio) {
        this.eventoRepositorio = eventoRepositorio;
    }

    @Override
    public Set<String> buscarTagsDoEvento(UUID eventoId) {
        return eventoRepositorio.buscarPorId(new EventoId(eventoId))
                .map(Evento::getTags)
                .orElse(Set.of());
    }

    @Override
    public boolean eventoEstaDisponivel(UUID eventoId) {
        return eventoRepositorio.buscarPorId(new EventoId(eventoId))
                .map(e -> e.estaAtivo() && e.possuiVagas())
                .orElse(false);
    }

    @Override
    public List<EventoCandidato> buscarEventosCandidatosPorTags(Set<String> tags) {
        return eventoRepositorio.buscarPorTags(tags).stream()
                .filter(Evento::estaAtivo)
                .map(e -> new EventoCandidato(e.getId().getValor(), e.getTags()))
                .toList();
    }
}
