package br.voke.aplicacao.evento;

import br.voke.dominio.evento.faq.PerguntaFrequente;
import br.voke.dominio.evento.faq.PerguntaFrequenteServico;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListarFaqDoEventoCasoDeUso {

    private final PerguntaFrequenteServico servico;

    public ListarFaqDoEventoCasoDeUso(PerguntaFrequenteServico servico) {
        this.servico = Objects.requireNonNull(servico);
    }

    public List<PerguntaFrequente> executar(UUID eventoId) {
        return servico.listarPorEvento(eventoId);
    }
}
