package br.voke.dominio.compartilhado;

import java.util.Objects;

public final class Senha {

    private final String valor;

    public Senha(String valor) {
        Objects.requireNonNull(valor, "Senha é obrigatória");
        if (valor.length() < 8) throw new IllegalArgumentException("Senha deve ter no mínimo 8 caracteres");
        if (!valor.matches(".*[a-zA-Z].*")) throw new IllegalArgumentException("Senha deve conter pelo menos uma letra");
        if (!valor.matches(".*[0-9].*")) throw new IllegalArgumentException("Senha deve conter pelo menos um número");
        this.valor = valor;
    }

    public String getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Senha)) return false;
        return valor.equals(((Senha) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }
}
