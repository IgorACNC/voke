package br.voke.infraestrutura.evento.estatistica;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "estatistica_evento", indexes = {
        @Index(name = "idx_est_organizador", columnList = "organizador_id"),
        @Index(name = "idx_est_evento", columnList = "evento_id", unique = true)
})
public class EstatisticaEventoJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "evento_id", nullable = false)
    private UUID eventoId;

    @Column(name = "organizador_id", nullable = false)
    private UUID organizadorId;

    @Column(name = "ingressos_vendidos", nullable = false)
    private int ingressosVendidos;

    @Column(name = "receita_consolidada", nullable = false, precision = 12, scale = 2)
    private BigDecimal receitaConsolidada;

    @Column(name = "check_ins_realizados", nullable = false)
    private int checkInsRealizados;

    @Column(name = "ausencias", nullable = false)
    private int ausencias;

    @Column(name = "cupons_utilizados", nullable = false)
    private int cuponsUtilizados;

    @Column(name = "desconto_acumulado", nullable = false, precision = 12, scale = 2)
    private BigDecimal descontoAcumulado;

    @Column(name = "visualizacoes", nullable = false)
    private int visualizacoes;

    @Column(name = "congelado", nullable = false)
    private boolean congelado;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    protected EstatisticaEventoJpa() {}

    public EstatisticaEventoJpa(UUID id, UUID eventoId, UUID organizadorId, int ingressosVendidos,
                                BigDecimal receitaConsolidada, int checkInsRealizados, int ausencias,
                                int cuponsUtilizados, BigDecimal descontoAcumulado, int visualizacoes,
                                boolean congelado, LocalDateTime atualizadoEm) {
        this.id = id;
        this.eventoId = eventoId;
        this.organizadorId = organizadorId;
        this.ingressosVendidos = ingressosVendidos;
        this.receitaConsolidada = receitaConsolidada;
        this.checkInsRealizados = checkInsRealizados;
        this.ausencias = ausencias;
        this.cuponsUtilizados = cuponsUtilizados;
        this.descontoAcumulado = descontoAcumulado;
        this.visualizacoes = visualizacoes;
        this.congelado = congelado;
        this.atualizadoEm = atualizadoEm;
    }

    public UUID getId() { return id; }
    public UUID getEventoId() { return eventoId; }
    public UUID getOrganizadorId() { return organizadorId; }
    public int getIngressosVendidos() { return ingressosVendidos; }
    public BigDecimal getReceitaConsolidada() { return receitaConsolidada; }
    public int getCheckInsRealizados() { return checkInsRealizados; }
    public int getAusencias() { return ausencias; }
    public int getCuponsUtilizados() { return cuponsUtilizados; }
    public BigDecimal getDescontoAcumulado() { return descontoAcumulado; }
    public int getVisualizacoes() { return visualizacoes; }
    public boolean isCongelado() { return congelado; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
