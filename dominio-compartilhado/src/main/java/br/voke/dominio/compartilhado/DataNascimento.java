package br.voke.dominio.compartilhado;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public final class DataNascimento {

    private final LocalDate valor;

    public DataNascimento(LocalDate valor) {
        Objects.requireNonNull(valor, "Data de nascimento é obrigatória");
        if (valor.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento não pode ser no futuro");
        }
        this.valor = valor;
    }

    public int idadeEmAnos() {
        return Period.between(valor, LocalDate.now()).getYears();
    }

    public LocalDate getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataNascimento)) return false;
        return valor.equals(((DataNascimento) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor.toString(); }
}
