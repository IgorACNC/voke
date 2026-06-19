package br.voke.aplicacao.evento;

import br.voke.dominio.evento.faq.PerguntaFrequenteId;
import br.voke.dominio.evento.faq.PerguntaFrequenteServico;

import java.util.Objects;
import java.util.UUID;

public class ExcluirPerguntaFaqCasoDeUso {

    private final PerguntaFrequenteServico servico;

    public ExcluirPerguntaFaqCasoDeUso(PerguntaFrequenteServico servico) {
        this.servico = Objects.requireNonNull(servico);
    }

    public void executar(UUID id) {
        servico.excluir(new PerguntaFrequenteId(id));
    }
}
