package br.voke.dominio.evento.subgrupo.solicitacao;

import java.util.Objects;
import java.util.UUID;

public final class SolicitacaoSubgrupoId {

    private final UUID valor;

    public SolicitacaoSubgrupoId(UUID valor) {
        Objects.requireNonNull(valor, "Id da solicitação é obrigatório");
        this.valor = valor;
    }

    public static SolicitacaoSubgrupoId novo() {
        return new SolicitacaoSubgrupoId(UUID.randomUUID());
    }

    public UUID getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SolicitacaoSubgrupoId)) return false;
        return valor.equals(((SolicitacaoSubgrupoId) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor.toString(); }
}
