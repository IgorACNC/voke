package br.voke.aplicacao.evento;

import br.voke.dominio.evento.faq.PerguntaFrequente;
import br.voke.dominio.evento.faq.PerguntaFrequenteServico;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ReordenarFaqCasoDeUso {

    private final PerguntaFrequenteServico servico;

    public ReordenarFaqCasoDeUso(PerguntaFrequenteServico servico) {
        this.servico = Objects.requireNonNull(servico);
    }

    public List<PerguntaFrequente> executar(UUID eventoId, List<UUID> idsOrdenados) {
        return servico.reordenar(eventoId, idsOrdenados);
    }
}
