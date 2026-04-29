package br.voke.dominio.evento.evento;

import java.math.BigDecimal;
import java.util.Objects;

public class Lote {

    private final int numero;
    private final BigDecimal preco;
    private final int quantidadeTotal;
    private int quantidadeVendida;
    private boolean ativo;

    public Lote(int numero, BigDecimal preco, int quantidadeTotal) {
        Objects.requireNonNull(preco, "Preço é obrigatório");
        if (preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo");
        }
        if (quantidadeTotal <= 0) {
            throw new IllegalArgumentException("Quantidade de ingressos deve ser maior que zero");
        }
        this.numero = numero;
        this.preco = preco;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeVendida = 0;
        this.ativo = true;
    }

    public boolean possuiVagas() {
        return quantidadeVendida < quantidadeTotal;
    }

    public int vagasDisponiveis() {
        return quantidadeTotal - quantidadeVendida;
    }

    public void venderIngresso() {
        if (!possuiVagas()) {
            throw new IllegalStateException("Lote esgotado");
        }
        quantidadeVendida++;
    }

    public void cancelarVenda() {
        if (quantidadeVendida > 0) {
            quantidadeVendida--;
        }
    }

    public void encerrar() {
        this.ativo = false;
    }

    public int getNumero() { return numero; }
    public BigDecimal getPreco() { return preco; }
    public int getQuantidadeTotal() { return quantidadeTotal; }
    public int getQuantidadeVendida() { return quantidadeVendida; }
    public boolean isAtivo() { return ativo; }
}
