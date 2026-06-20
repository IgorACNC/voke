package br.voke.aplicacao.evento;

import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.evento.EventoServico;
import br.voke.dominio.evento.grupo.GrupoEventoServico;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class EncerrarEventosExpiradosCasoDeUso {

    private final EventoServico servico;
    private final EventoRepositorio eventoRepositorio;
    private final AtualizadorEstatisticaListener atualizadorEstatistica;
    private final GrupoEventoServico grupoEventoServico;

    public EncerrarEventosExpiradosCasoDeUso(EventoServico servico,
                                             EventoRepositorio eventoRepositorio,
                                             AtualizadorEstatisticaListener atualizadorEstatistica,
                                             GrupoEventoServico grupoEventoServico) {
        Objects.requireNonNull(servico);
        Objects.requireNonNull(eventoRepositorio);
        Objects.requireNonNull(atualizadorEstatistica);
        Objects.requireNonNull(grupoEventoServico);
        this.servico = servico;
        this.eventoRepositorio = eventoRepositorio;
        this.atualizadorEstatistica = atualizadorEstatistica;
        this.grupoEventoServico = grupoEventoServico;
    }

    public int executar() {
        LocalDateTime agora = LocalDateTime.now();
        List<Evento> expirados = eventoRepositorio.buscarExpirados(agora);
        int quantidade = servico.encerrarExpirados(agora);
        expirados.forEach(e -> {
            // RN05 (dashboard): congela snapshot do evento encerrado
            atualizadorEstatistica.aoEncerrarEvento(e.getId().getValor());
            // RN3 (grupos): remove o grupo de comunicação vinculado
            grupoEventoServico.removerPorEvento(e.getId().getValor());
        });
        return quantidade;
    }
}
