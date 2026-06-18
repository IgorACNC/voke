package br.voke.dominio.evento.estatistica;

import br.voke.dominio.compartilhado.EntidadeBase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class EstatisticaEvento extends EntidadeBase<EstatisticaEventoId> {

    private final UUID eventoId;
    private final UUID organizadorId;
    private int ingressosVendidos;
    private BigDecimal receitaConsolidada;
    private int checkInsRealizados;
    private int ausencias;
    private int cuponsUtilizados;
    private BigDecimal descontoAcumulado;
    private int visualizacoes;
    private boolean congelado;
    private LocalDateTime atualizadoEm;

    public EstatisticaEvento(EstatisticaEventoId id, UUID eventoId, UUID organizadorId) {
        super(id);
        Objects.requireNonNull(eventoId, "EventoId e obrigatorio");
        Objects.requireNonNull(organizadorId, "OrganizadorId e obrigatorio");
        this.eventoId = eventoId;
        this.organizadorId = organizadorId;
        this.ingressosVendidos = 0;
        this.receitaConsolidada = BigDecimal.ZERO;
        this.checkInsRealizados = 0;
        this.ausencias = 0;
        this.cuponsUtilizados = 0;
        this.descontoAcumulado = BigDecimal.ZERO;
        this.visualizacoes = 0;
        this.congelado = false;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void registrarInscricaoConfirmada(BigDecimal valor) {
        garantirNaoCongelada();
        Objects.requireNonNull(valor, "Valor e obrigatorio");
        this.ingressosVendidos += 1;
        this.receitaConsolidada = this.receitaConsolidada.add(valor);
        this.atualizadoEm = LocalDateTime.now();
    }

    public void registrarCancelamento(BigDecimal valorEstornado) {
        garantirNaoCongelada();
        Objects.requireNonNull(valorEstornado, "Valor estornado e obrigatorio");
        if (this.ingressosVendidos > 0) this.ingressosVendidos -= 1;
        this.receitaConsolidada = this.receitaConsolidada.subtract(valorEstornado);
        if (this.receitaConsolidada.signum() < 0) this.receitaConsolidada = BigDecimal.ZERO;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void registrarCheckIn() {
        garantirNaoCongelada();
        this.checkInsRealizados += 1;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void registrarAusencia() {
        garantirNaoCongelada();
        this.ausencias += 1;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void registrarCupomUsado(BigDecimal desconto) {
        garantirNaoCongelada();
        Objects.requireNonNull(desconto, "Desconto e obrigatorio");
        this.cuponsUtilizados += 1;
        this.descontoAcumulado = this.descontoAcumulado.add(desconto);
        this.atualizadoEm = LocalDateTime.now();
    }

    public void registrarVisualizacao() {
        // Visualizacao registrada mesmo apos congelamento? Nao - mantemos read-only.
        garantirNaoCongelada();
        this.visualizacoes += 1;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void congelar() {
        this.congelado = true;
        this.atualizadoEm = LocalDateTime.now();
    }

    private void garantirNaoCongelada() {
        if (congelado) {
            throw new EstatisticaCongeladaException();
        }
    }

    public UUID getEventoId() { return eventoId; }
    public UUID getOrganizadorId() { return organizadorId; }
    public int getIngressosVendidos() { return ingressosVendidos; }
    public BigDecimal getReceitaConsolidada() { return receitaConsolidada; }
    public int getCheckInsRealizados() { return checkInsRealizados; }
    public int getAusencias() { return ausencias; }
    public int getCuponsUtilizados() { return cuponsUtilizados; }
    public BigDecimal getDescontoAcumulado() { return descontoAcumulado; }
    public int getVisualizacoes() { return visualizacoes; }
    public boolean estaCongelada() { return congelado; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
