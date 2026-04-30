package br.voke.aplicacao.evento;

import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoServico;

import java.util.Objects;
import java.util.UUID;

public class CancelarEventoCasoDeUso {

    private final EventoServico servico;

    public CancelarEventoCasoDeUso(EventoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID eventoId) {
        servico.cancelar(new EventoId(eventoId));
    }
}
