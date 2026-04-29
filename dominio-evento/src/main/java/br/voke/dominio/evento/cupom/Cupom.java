package br.voke.dominio.evento.cupom;

import br.voke.dominio.compartilhado.EntidadeBase;
import br.voke.dominio.evento.excecao.CupomEsgotadoException;
import br.voke.dominio.evento.excecao.CupomJaUtilizadoException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Cupom extends EntidadeBase<CupomId> {

    private String codigo;
    private BigDecimal desconto;
    private final UUID organizadorId;
    private final UUID eventoId; // null = cupom global
    private int quantidadeMaxima;
    private final Set<String> cpfsUtilizados;

    public Cupom(CupomId id, String codigo, BigDecimal desconto, UUID organizadorId,
                 UUID eventoId, int quantidadeMaxima) {
        super(id);
        Objects.requireNonNull(codigo, "Código é obrigatório");
        Objects.requireNonNull(desconto, "Desconto é obrigatório");
        Objects.requireNonNull(organizadorId, "Organizador é obrigatório");
        if (desconto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Desconto deve ser maior que zero");
        }
        if (quantidadeMaxima <= 0) {
            throw new IllegalArgumentException("Quantidade máxima deve ser maior que zero");
        }
        this.codigo = codigo;
        this.desconto = desconto;
        this.organizadorId = organizadorId;
        this.eventoId = eventoId;
        this.quantidadeMaxima = quantidadeMaxima;
        this.cpfsUtilizados = new HashSet<>();
    }

    public void utilizar(String cpf) {
        Objects.requireNonNull(cpf, "CPF é obrigatório");
        if (cpfsUtilizados.contains(cpf)) {
            throw new CupomJaUtilizadoException();
        }
        if (cpfsUtilizados.size() >= quantidadeMaxima) {
            throw new CupomEsgotadoException();
        }
        cpfsUtilizados.add(cpf);
    }

    public boolean isGlobal() {
        return eventoId == null;
    }

    public boolean estaDisponivel() {
        return cpfsUtilizados.size() < quantidadeMaxima;
    }

    public void atualizarDesconto(BigDecimal novoDesconto) {
        Objects.requireNonNull(novoDesconto, "Desconto é obrigatório");
        if (!cpfsUtilizados.isEmpty()) {
            throw new IllegalStateException("Não é possível alterar o desconto de um cupom já utilizado");
        }
        this.desconto = novoDesconto;
    }

    public void atualizarQuantidadeMaxima(int novaQuantidade) {
        if (novaQuantidade <= 0) {
            throw new IllegalArgumentException("Quantidade máxima deve ser maior que zero");
        }
        this.quantidadeMaxima = novaQuantidade;
    }

    public String getCodigo() { return codigo; }
    public BigDecimal getDesconto() { return desconto; }
    public UUID getOrganizadorId() { return organizadorId; }
    public UUID getEventoId() { return eventoId; }
    public int getQuantidadeMaxima() { return quantidadeMaxima; }
    public int getQuantidadeUtilizada() { return cpfsUtilizados.size(); }
    public Set<String> getCpfsUtilizados() { return Collections.unmodifiableSet(cpfsUtilizados); }
}
