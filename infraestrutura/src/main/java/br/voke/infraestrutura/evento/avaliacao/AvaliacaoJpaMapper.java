package br.voke.infraestrutura.evento.avaliacao;

import br.voke.dominio.evento.avaliacao.Avaliacao;
import br.voke.dominio.evento.avaliacao.AvaliacaoId;

public final class AvaliacaoJpaMapper {

    private AvaliacaoJpaMapper() {
    }

    public static AvaliacaoJpa paraJpa(Avaliacao avaliacao) {
        return new AvaliacaoJpa(avaliacao.getId().getValor(), avaliacao.getParticipanteId(),
                avaliacao.getEventoId(), avaliacao.getNota(), avaliacao.getComentario());
    }

    public static Avaliacao paraDominio(AvaliacaoJpa jpa) {
        return new Avaliacao(new AvaliacaoId(jpa.getId()), jpa.getParticipanteId(),
                jpa.getEventoId(), jpa.getNota(), jpa.getComentario());
    }
}
