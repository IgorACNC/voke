package br.voke.infraestrutura.pessoa.organizador;

import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.evento.StatusEvento;
import br.voke.dominio.pessoa.organizador.EventosOrganizadorGateway;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EventosOrganizadorGatewayAdapter implements EventosOrganizadorGateway {

    private final EventoRepositorio eventoRepositorio;

    public EventosOrganizadorGatewayAdapter(EventoRepositorio eventoRepositorio) {
        this.eventoRepositorio = eventoRepositorio;
    }

    @Override
    public boolean possuiEventosAtivos(UUID organizadorId) {
        return eventoRepositorio.buscarPorOrganizador(organizadorId).stream()
                .map(Evento::getStatus)
                .anyMatch(s -> s == StatusEvento.PUBLICADO || s == StatusEvento.ATIVO);
    }
}
