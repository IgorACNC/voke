package br.voke.dominio.compartilhado;

import java.util.Objects;

public final class Email {

    private final String valor;

    public Email(String valor) {
        Objects.requireNonNull(valor, "E-mail é obrigatório");
        if (valor.isBlank()) throw new IllegalArgumentException("E-mail é obrigatório");
        if (!valor.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("E-mail inválido");
        }
        this.valor = valor.toLowerCase().trim();
    }

    public String getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        return valor.equals(((Email) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor; }
}
