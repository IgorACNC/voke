package br.voke.dominio.compartilhado;

import java.util.Objects;

public final class NomeCompleto {

    private final String valor;

    public NomeCompleto(String valor) {
        Objects.requireNonNull(valor, "Nome é obrigatório");
        if (valor.isBlank()) throw new IllegalArgumentException("Nome não pode ser vazio");
        if (valor.trim().length() < 3) throw new IllegalArgumentException("Nome deve ter pelo menos 3 caracteres");
        this.valor = valor.trim();
    }

    public String getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NomeCompleto)) return false;
        return valor.equals(((NomeCompleto) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor; }
}
