package br.voke.aplicacao.evento;

import br.voke.dominio.evento.estatistica.EstatisticaEvento;
import br.voke.dominio.evento.estatistica.EstatisticaEventoId;
import br.voke.dominio.evento.estatistica.EstatisticaEventoRepositorio;
import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Listener sincrono que atualiza o snapshot de estatistica (RN03 - F17).
 * Eh chamado diretamente pelos casos de uso (RealizarInscricao, CancelarInscricao,
 * RealizarCheckIn, CancelarEvento, EncerrarEventosExpirados) na mesma transacao.
 *
 * Decisao: sincrono porque o projeto nao tem @Async configurado.
 * Documentado como divida tecnica no README.
 */
public class AtualizadorEstatisticaListener {

    private final EstatisticaEventoRepositorio estatisticaRepositorio;
    private final EventoRepositorio eventoRepositorio;

    public AtualizadorEstatisticaListener(EstatisticaEventoRepositorio estatisticaRepositorio,
                                          EventoRepositorio eventoRepositorio) {
        Objects.requireNonNull(estatisticaRepositorio);
        Objects.requireNonNull(eventoRepositorio);
        this.estatisticaRepositorio = estatisticaRepositorio;
        this.eventoRepositorio = eventoRepositorio;
    }

    public void aoConfirmarInscricao(UUID eventoId, BigDecimal valorPago) {
        EstatisticaEvento estatistica = obterOuCriar(eventoId);
        if (estatistica.estaCongelada()) return; // RN05: ignora silenciosamente
        estatistica.registrarInscricaoConfirmada(valorPago);
        estatisticaRepositorio.salvar(estatistica);
    }

    public void aoCancelarInscricao(UUID eventoId, BigDecimal valorEstornado) {
        EstatisticaEvento estatistica = obterOuCriar(eventoId);
        if (estatistica.estaCongelada()) return;
        estatistica.registrarCancelamento(valorEstornado);
        estatisticaRepositorio.salvar(estatistica);
    }

    public void aoRealizarCheckIn(UUID eventoId) {
        EstatisticaEvento estatistica = obterOuCriar(eventoId);
        if (estatistica.estaCongelada()) return;
        estatistica.registrarCheckIn();
        estatisticaRepositorio.salvar(estatistica);
    }

    public void aoEncerrarEvento(UUID eventoId) {
        EstatisticaEvento estatistica = obterOuCriar(eventoId);
        estatistica.congelar();
        estatisticaRepositorio.salvar(estatistica);
    }

    private EstatisticaEvento obterOuCriar(UUID eventoId) {
        return estatisticaRepositorio.buscarPorEventoId(eventoId)
                .orElseGet(() -> {
                    Evento evento = eventoRepositorio.buscarPorId(new EventoId(eventoId))
                            .orElseThrow(() -> new IllegalArgumentException("Evento nao encontrado"));
                    return new EstatisticaEvento(EstatisticaEventoId.novo(),
                            eventoId, evento.getOrganizadorId());
                });
    }
}
