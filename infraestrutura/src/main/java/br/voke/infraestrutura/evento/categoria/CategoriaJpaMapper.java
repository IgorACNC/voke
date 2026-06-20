package br.voke.infraestrutura.evento.categoria;

import br.voke.dominio.evento.categoria.Categoria;
import br.voke.dominio.evento.categoria.CategoriaId;

public final class CategoriaJpaMapper {

    private CategoriaJpaMapper() {}

    public static CategoriaJpa paraJpa(Categoria categoria) {
        return new CategoriaJpa(categoria.getId().getValor(), categoria.getNome());
    }

    public static Categoria paraDominio(CategoriaJpa jpa) {
        return new Categoria(new CategoriaId(jpa.getId()), jpa.getNome());
    }
}
