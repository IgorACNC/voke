package br.voke.infraestrutura.evento.evento;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class LoteJpa {

    private int numero;
    private BigDecimal preco;
    private int quantidadeTotal;
    private int quantidadeVendida;
    private boolean ativo;

    protected LoteJpa() {
    }

    public LoteJpa(int numero, BigDecimal preco, int quantidadeTotal, int quantidadeVendida, boolean ativo) {
        this.numero = numero;
        this.preco = preco;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeVendida = quantidadeVendida;
        this.ativo = ativo;
    }

    public int getNumero() { return numero; }
    public BigDecimal getPreco() { return preco; }
    public int getQuantidadeTotal() { return quantidadeTotal; }
    public int getQuantidadeVendida() { return quantidadeVendida; }
    public boolean isAtivo() { return ativo; }
}
