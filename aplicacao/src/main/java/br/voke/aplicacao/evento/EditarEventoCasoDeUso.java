package br.voke.aplicacao.evento;

import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoServico;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class EditarEventoCasoDeUso {

    private final EventoServico servico;

    public EditarEventoCasoDeUso(EventoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID eventoId, String novoNome, String novoLocal,
                         LocalDateTime novoInicio, LocalDateTime novoFim, int novaCapacidade) {
        servico.editar(new EventoId(eventoId), novoNome, novoLocal, novoInicio, novoFim, novaCapacidade);
    }
}
