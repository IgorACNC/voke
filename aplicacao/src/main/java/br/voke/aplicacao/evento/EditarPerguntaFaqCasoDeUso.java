package br.voke.aplicacao.evento;

import br.voke.dominio.evento.faq.PerguntaFrequente;
import br.voke.dominio.evento.faq.PerguntaFrequenteId;
import br.voke.dominio.evento.faq.PerguntaFrequenteServico;

import java.util.Objects;
import java.util.UUID;

public class EditarPerguntaFaqCasoDeUso {

    private final PerguntaFrequenteServico servico;

    public EditarPerguntaFaqCasoDeUso(PerguntaFrequenteServico servico) {
        this.servico = Objects.requireNonNull(servico);
    }

    public PerguntaFrequente executar(UUID id, String pergunta, String resposta) {
        return servico.editar(new PerguntaFrequenteId(id), pergunta, resposta);
    }
}
