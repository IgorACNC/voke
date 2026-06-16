package br.voke.aplicacao.evento;

import br.voke.dominio.evento.evento.EventoServico;

import java.time.LocalDateTime;
import java.util.Objects;

public class EncerrarEventosExpiradosCasoDeUso {

    private final EventoServico servico;

    public EncerrarEventosExpiradosCasoDeUso(EventoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public int executar() {
        return servico.encerrarExpirados(LocalDateTime.now());
    }
}
