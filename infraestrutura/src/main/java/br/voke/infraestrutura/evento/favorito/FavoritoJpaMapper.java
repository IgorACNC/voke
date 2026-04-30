package br.voke.infraestrutura.evento.favorito;

import br.voke.dominio.evento.favorito.Favorito;
import br.voke.dominio.evento.favorito.FavoritoId;

public final class FavoritoJpaMapper {

    private FavoritoJpaMapper() {
    }

    public static FavoritoJpa paraJpa(Favorito favorito) {
        return new FavoritoJpa(favorito.getId().getValor(), favorito.getParticipanteId(), favorito.getEventoId());
    }

    public static Favorito paraDominio(FavoritoJpa jpa) {
        return new Favorito(new FavoritoId(jpa.getId()), jpa.getParticipanteId(), jpa.getEventoId());
    }
}