package br.voke.infraestrutura.evento.evento;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class LoteJpa {

    @Column(name = "lote_numero")
    private int numero;

    @Column(name = "lote_preco", precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "lote_quantidade_total")
    private int quantidadeTotal;

    @Column(name = "lote_quantidade_vendida")
    private int quantidadeVendida;

    @Column(name = "lote_ativo")
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
