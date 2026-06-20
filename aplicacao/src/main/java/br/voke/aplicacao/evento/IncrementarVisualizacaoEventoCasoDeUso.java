package br.voke.aplicacao.evento;

import br.voke.dominio.evento.estatistica.EstatisticaEvento;
import br.voke.dominio.evento.estatistica.EstatisticaEventoId;
import br.voke.dominio.evento.estatistica.EstatisticaEventoRepositorio;
import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;

import java.util.Objects;
import java.util.UUID;

public class IncrementarVisualizacaoEventoCasoDeUso {

    private final EventoRepositorio eventoRepositorio;
    private final EstatisticaEventoRepositorio estatisticaRepositorio;

    public IncrementarVisualizacaoEventoCasoDeUso(EventoRepositorio eventoRepositorio,
                                                  EstatisticaEventoRepositorio estatisticaRepositorio) {
        Objects.requireNonNull(eventoRepositorio);
        Objects.requireNonNull(estatisticaRepositorio);
        this.eventoRepositorio = eventoRepositorio;
        this.estatisticaRepositorio = estatisticaRepositorio;
    }

    public void executar(UUID eventoId) {
        Objects.requireNonNull(eventoId, "eventoId e obrigatorio");
        Evento evento = eventoRepositorio.buscarPorId(new EventoId(eventoId))
                .orElseThrow(() -> new IllegalArgumentException("Evento nao encontrado"));
        evento.incrementarVisualizacoes();
        eventoRepositorio.salvar(evento);

        EstatisticaEvento estatistica = estatisticaRepositorio.buscarPorEventoId(eventoId)
                .orElseGet(() -> new EstatisticaEvento(EstatisticaEventoId.novo(),
                        eventoId, evento.getOrganizadorId()));
        // RN05: se congelada, ignora silenciosamente (evento encerrado)
        if (!estatistica.estaCongelada()) {
            estatistica.registrarVisualizacao();
            estatisticaRepositorio.salvar(estatistica);
        }
    }
}
