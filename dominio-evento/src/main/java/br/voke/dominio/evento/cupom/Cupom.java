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
    private TipoDesconto tipoDesconto;
    private final UUID organizadorId;
    private final UUID eventoId; // null = cupom global
    private int quantidadeMaxima;
    private boolean ativo;
    private final Set<String> cpfsUtilizados;

    public Cupom(CupomId id, String codigo, BigDecimal desconto, UUID organizadorId,
                 UUID eventoId, int quantidadeMaxima) {
        this(id, codigo, desconto, TipoDesconto.FIXO, organizadorId, eventoId, quantidadeMaxima);
    }

    public Cupom(CupomId id, String codigo, BigDecimal desconto, TipoDesconto tipoDesconto,
                 UUID organizadorId, UUID eventoId, int quantidadeMaxima) {
        super(id);
        Objects.requireNonNull(codigo, "Código é obrigatório");
        Objects.requireNonNull(desconto, "Desconto é obrigatório");
        Objects.requireNonNull(tipoDesconto, "Tipo de desconto é obrigatório");
        if (desconto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Desconto deve ser maior que zero");
        }
        if (tipoDesconto == TipoDesconto.PERCENTUAL && desconto.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Desconto percentual não pode ultrapassar 100%");
        }
        if (quantidadeMaxima <= 0) {
            throw new IllegalArgumentException("Quantidade máxima deve ser maior que zero");
        }
        this.codigo = codigo;
        this.desconto = desconto;
        this.tipoDesconto = tipoDesconto;
        this.organizadorId = organizadorId;
        this.eventoId = eventoId;
        this.quantidadeMaxima = quantidadeMaxima;
        this.ativo = true;
        this.cpfsUtilizados = new HashSet<>();
    }

    public void utilizar(String cpf) {
        Objects.requireNonNull(cpf, "CPF é obrigatório");
        if (!ativo) {
            throw new IllegalStateException("Cupom inativo");
        }
        if (cpfsUtilizados.contains(cpf)) {
            throw new CupomJaUtilizadoException();
        }
        if (cpfsUtilizados.size() >= quantidadeMaxima) {
            throw new CupomEsgotadoException();
        }
        cpfsUtilizados.add(cpf);
    }

    public void ativar() { this.ativo = true; }
    public void desativar() { this.ativo = false; }

    public void liberarUso(String cpf) {
        if (cpf != null) cpfsUtilizados.remove(cpf);
    }

    public boolean aplicavelAoEvento(UUID eventoIdCarrinho, UUID organizadorIdEvento) {
        // Cupom global de admin: vale para qualquer evento
        if (this.organizadorId == null) return true;
        // Cupom de organizador vinculado a evento específico: precisa bater o evento
        if (this.eventoId != null) return this.eventoId.equals(eventoIdCarrinho);
        // Cupom de organizador para todos os seus eventos: precisa bater o organizador do evento
        return this.organizadorId.equals(organizadorIdEvento);
    }

    public boolean isGlobal() {
        return organizadorId == null && eventoId == null;
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

    public BigDecimal calcularDesconto(BigDecimal subtotal) {
        if (tipoDesconto == TipoDesconto.PERCENTUAL) {
            return subtotal.multiply(desconto)
                    .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
        }
        return desconto;
    }

    public String getCodigo() { return codigo; }
    public BigDecimal getDesconto() { return desconto; }
    public TipoDesconto getTipoDesconto() { return tipoDesconto; }
    public UUID getOrganizadorId() { return organizadorId; }
    public UUID getEventoId() { return eventoId; }
    public int getQuantidadeMaxima() { return quantidadeMaxima; }
    public int getQuantidadeUtilizada() { return cpfsUtilizados.size(); }
    public boolean isAtivo() { return ativo; }
    public Set<String> getCpfsUtilizados() { return Collections.unmodifiableSet(cpfsUtilizados); }
}
