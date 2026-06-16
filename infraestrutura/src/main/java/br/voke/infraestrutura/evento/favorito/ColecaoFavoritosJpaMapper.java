package br.voke.infraestrutura.evento.favorito;

import br.voke.dominio.evento.favorito.ColecaoFavoritos;
import br.voke.dominio.evento.favorito.ColecaoFavoritosId;
import br.voke.dominio.evento.favorito.ItemColecao;

import java.util.List;

public final class ColecaoFavoritosJpaMapper {

    private ColecaoFavoritosJpaMapper() {}

    public static ColecaoFavoritosJpa paraJpa(ColecaoFavoritos colecao) {
        List<ItemColecaoJpa> itensJpa = colecao.getItens().stream()
                .map(i -> new ItemColecaoJpa(i.getEventoId(), i.getOrdem()))
                .toList();
        return new ColecaoFavoritosJpa(
                colecao.getId().getValor(),
                colecao.getNome(),
                colecao.getVisibilidade(),
                colecao.getDataCriacao(),
                colecao.getParticipanteId(),
                itensJpa);
    }

    public static ColecaoFavoritos paraDominio(ColecaoFavoritosJpa jpa) {
        List<ItemColecao> itens = jpa.getItens().stream()
                .map(i -> new ItemColecao(i.getEventoId(), i.getOrdem()))
                .toList();
        return new ColecaoFavoritos(
                new ColecaoFavoritosId(jpa.getId()),
                jpa.getNome(),
                jpa.getVisibilidade(),
                jpa.getDataCriacao(),
                jpa.getParticipanteId(),
                itens);
    }
}
