package br.voke.aplicacao.evento;

import br.voke.dominio.evento.faq.PerguntaFrequente;
import br.voke.dominio.evento.faq.PerguntaFrequenteServico;

import java.util.Objects;
import java.util.UUID;

public class CriarPerguntaFaqCasoDeUso {

    private final PerguntaFrequenteServico servico;

    public CriarPerguntaFaqCasoDeUso(PerguntaFrequenteServico servico) {
        this.servico = Objects.requireNonNull(servico);
    }

    public PerguntaFrequente executar(UUID eventoId, String pergunta, String resposta) {
        return servico.criar(eventoId, pergunta, resposta);
    }
}
