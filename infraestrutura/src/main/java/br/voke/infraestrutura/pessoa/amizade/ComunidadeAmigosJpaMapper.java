package br.voke.infraestrutura.pessoa.amizade;

import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.pessoa.amizade.ComunidadeAmigos;
import br.voke.dominio.pessoa.amizade.ComunidadeAmigosId;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.stream.Collectors;

public final class ComunidadeAmigosJpaMapper {

    private ComunidadeAmigosJpaMapper() {
    }

    public static ComunidadeAmigosJpa paraJpa(ComunidadeAmigos comunidade) {
        return new ComunidadeAmigosJpa(
                comunidade.getId().getValor(),
                comunidade.getNome().getValor(),
                comunidade.getCriadorId().getValor(),
                comunidade.getMembros().stream().map(ParticipanteId::getValor).collect(Collectors.toSet()),
                comunidade.getEventoCompartilhadoIds());
    }

    public static ComunidadeAmigos paraDominio(ComunidadeAmigosJpa jpa) {
        ComunidadeAmigos comunidade = new ComunidadeAmigos(
                new ComunidadeAmigosId(jpa.getId()),
                new NomeCompleto(jpa.getNome()),
                new ParticipanteId(jpa.getCriadorId()));
        jpa.getMembros().stream()
                .filter(id -> !id.equals(jpa.getCriadorId()))
                .map(ParticipanteId::new)
                .forEach(comunidade::adicionarMembro);
        jpa.getEventoCompartilhadoIds().forEach(comunidade::compartilharEvento);
        return comunidade;
    }
}
