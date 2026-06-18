package br.voke.aplicacao.evento;

import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.evento.EventoServico;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class EncerrarEventosExpiradosCasoDeUso {

    private final EventoServico servico;
    private final EventoRepositorio eventoRepositorio;
    private final AtualizadorEstatisticaListener atualizadorEstatistica;

    public EncerrarEventosExpiradosCasoDeUso(EventoServico servico,
                                             EventoRepositorio eventoRepositorio,
                                             AtualizadorEstatisticaListener atualizadorEstatistica) {
        Objects.requireNonNull(servico);
        Objects.requireNonNull(eventoRepositorio);
        Objects.requireNonNull(atualizadorEstatistica);
        this.servico = servico;
        this.eventoRepositorio = eventoRepositorio;
        this.atualizadorEstatistica = atualizadorEstatistica;
    }

    public int executar() {
        LocalDateTime agora = LocalDateTime.now();
        List<Evento> expirados = eventoRepositorio.buscarExpirados(agora);
        int quantidade = servico.encerrarExpirados(agora);
        // RN05: congela snapshot de cada um dos encerrados
        expirados.forEach(e -> atualizadorEstatistica.aoEncerrarEvento(e.getId().getValor()));
        return quantidade;
    }
}
