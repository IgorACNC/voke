package br.voke.dominio.compartilhado;

import java.util.Objects;

public abstract class EntidadeBase<ID> {

    private final ID id;

    protected EntidadeBase(ID id) {
        Objects.requireNonNull(id, "Id é obrigatório");
        this.id = id;
    }

    public ID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntidadeBase<?> that = (EntidadeBase<?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
