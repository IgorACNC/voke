package br.voke.infraestrutura.evento.faq;

import br.voke.dominio.evento.faq.PerguntaFrequente;
import br.voke.dominio.evento.faq.PerguntaFrequenteId;

public final class PerguntaFrequenteJpaMapper {

    private PerguntaFrequenteJpaMapper() {}

    public static PerguntaFrequenteJpa paraJpa(PerguntaFrequente p) {
        return new PerguntaFrequenteJpa(
                p.getId().getValor(),
                p.getEventoId(),
                p.getPergunta(),
                p.getPerguntaNormalizada(),
                p.getResposta(),
                p.getPosicao());
    }

    public static PerguntaFrequente paraDominio(PerguntaFrequenteJpa jpa) {
        return new PerguntaFrequente(
                new PerguntaFrequenteId(jpa.getId()),
                jpa.getEventoId(),
                jpa.getPergunta(),
                jpa.getResposta(),
                jpa.getPosicao());
    }
}
