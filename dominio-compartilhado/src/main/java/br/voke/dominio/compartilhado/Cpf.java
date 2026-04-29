package br.voke.dominio.compartilhado;

import java.util.Objects;

public final class Cpf {

    private final String valor;

    public Cpf(String valor) {
        Objects.requireNonNull(valor, "CPF é obrigatório");
        String digitos = valor.replaceAll("[^0-9]", "");
        if (!algoritmoValido(digitos)) {
            throw new IllegalArgumentException("CPF inválido");
        }
        this.valor = digitos;
    }

    private static boolean algoritmoValido(String d) {
        if (d.length() != 11) return false;
        if (d.chars().distinct().count() == 1) return false;

        int soma = 0;
        for (int i = 0; i < 9; i++) soma += (d.charAt(i) - '0') * (10 - i);
        int r1 = (soma * 10) % 11;
        if (r1 == 10 || r1 == 11) r1 = 0;
        if (r1 != (d.charAt(9) - '0')) return false;

        soma = 0;
        for (int i = 0; i < 10; i++) soma += (d.charAt(i) - '0') * (11 - i);
        int r2 = (soma * 10) % 11;
        if (r2 == 10 || r2 == 11) r2 = 0;
        return r2 == (d.charAt(10) - '0');
    }

    public String getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cpf)) return false;
        return valor.equals(((Cpf) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor; }
}
