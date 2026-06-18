package br.voke.infraestrutura.fidelidade.comissao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import br.voke.dominio.fidelidade.comissao.StatusComissao;

@Entity
@Table(name = "comissoes_parceiro")
public class ComissaoParceiroJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID parceiroId;

    @Column(nullable = false)
    private UUID cupomId;

    @Column(nullable = false)
    private UUID inscricaoId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusComissao status;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    protected ComissaoParceiroJpa() {}

    public ComissaoParceiroJpa(UUID id, UUID parceiroId, UUID cupomId, UUID inscricaoId, BigDecimal valor, StatusComissao status, LocalDateTime dataHora) {
        this.id = id;
        this.parceiroId = parceiroId;
        this.cupomId = cupomId;
        this.inscricaoId = inscricaoId;
        this.valor = valor;
        this.status = status;
        this.dataHora = dataHora;
    }

    public UUID getId() { return id; }
    public UUID getParceiroId() { return parceiroId; }
    public UUID getCupomId() { return cupomId; }
    public UUID getInscricaoId() { return inscricaoId; }
    public BigDecimal getValor() { return valor; }
    public StatusComissao getStatus() { return status; }
    public LocalDateTime getDataHora() { return dataHora; }
}
