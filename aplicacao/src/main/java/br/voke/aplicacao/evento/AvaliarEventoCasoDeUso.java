package br.voke.aplicacao.evento;

import br.voke.dominio.evento.avaliacao.Avaliacao;
import br.voke.dominio.evento.avaliacao.AvaliacaoServico;

import java.util.Objects;
import java.util.UUID;

public class AvaliarEventoCasoDeUso {

    private final AvaliacaoServico servico;

    public AvaliarEventoCasoDeUso(AvaliacaoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public Avaliacao executar(UUID participanteId, UUID eventoId, int nota, String comentario,
                              boolean eventoFinalizado, boolean inscricaoConfirmada) {
        return servico.avaliar(participanteId, eventoId, nota, comentario,
                eventoFinalizado, inscricaoConfirmada);
    }
}
