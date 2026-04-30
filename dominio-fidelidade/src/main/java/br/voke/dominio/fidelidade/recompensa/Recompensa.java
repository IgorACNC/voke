package br.voke.dominio.fidelidade.recompensa;

import br.voke.dominio.compartilhado.EntidadeBase;
import br.voke.dominio.fidelidade.excecao.CongelamentoDePrecoException;
import br.voke.dominio.fidelidade.excecao.RecompensaEsgotadaException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Recompensa extends EntidadeBase<RecompensaId> {

    private static final int DIAS_CONGELAMENTO = 30;

    private String nome;
    private String descricao;
    private int custoEmPontos;
    private int estoqueTotal;
    private int estoqueResgatado;
    private final UUID organizadorId;
    private LocalDateTime ultimaAlteracaoValor;
    private boolean ativa;

    public Recompensa(RecompensaId id, String nome, String descricao, int custoEmPontos,
                      int estoqueTotal, UUID organizadorId) {
        super(id);
        Objects.requireNonNull(nome, "Nome é obrigatório");
        Objects.requireNonNull(organizadorId, "Organizador é obrigatório");
        if (custoEmPontos <= 0) {
            throw new IllegalArgumentException("Custo em pontos deve ser maior que zero");
        }
        if (estoqueTotal <= 0) {
            throw new IllegalArgumentException("Estoque deve ser maior que zero");
        }
        this.nome = nome;
        this.descricao = descricao;
        this.custoEmPontos = custoEmPontos;
        this.estoqueTotal = estoqueTotal;
        this.estoqueResgatado = 0;
        this.organizadorId = organizadorId;
        this.ultimaAlteracaoValor = LocalDateTime.now();
        this.ativa = true;
    }

    public void resgatar() {
        if (!estaDisponivel()) {
            throw new RecompensaEsgotadaException();
        }
        estoqueResgatado++;
        if (estoqueResgatado >= estoqueTotal) {
            this.ativa = false;
        }
    }

    public void alterarCusto(int novoCusto) {
        if (novoCusto <= 0) {
            throw new IllegalArgumentException("Custo em pontos deve ser maior que zero");
        }
        if (ultimaAlteracaoValor.plusDays(DIAS_CONGELAMENTO).isAfter(LocalDateTime.now())) {
            throw new CongelamentoDePrecoException();
        }
        this.custoEmPontos = novoCusto;
        this.ultimaAlteracaoValor = LocalDateTime.now();
    }

    public void inativar() {
        this.ativa = false;
    }

    public void atualizarDescricao(String novaDescricao) {
        Objects.requireNonNull(novaDescricao, "Descrição é obrigatória");
        this.descricao = novaDescricao;
    }

    public boolean estaDisponivel() {
        return ativa && estoqueResgatado < estoqueTotal;
    }

    public boolean foiResgatada() {
        return estoqueResgatado > 0;
    }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public int getCustoEmPontos() { return custoEmPontos; }
    public int getEstoqueTotal() { return estoqueTotal; }
    public int getEstoqueResgatado() { return estoqueResgatado; }
    public int getEstoqueRestante() { return estoqueTotal - estoqueResgatado; }
    public UUID getOrganizadorId() { return organizadorId; }
    public LocalDateTime getUltimaAlteracaoValor() { return ultimaAlteracaoValor; }
    public boolean isAtiva() { return ativa; }
}
