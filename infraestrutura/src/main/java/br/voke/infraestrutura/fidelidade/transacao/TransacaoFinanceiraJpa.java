package br.voke.infraestrutura.fidelidade.transacao;

import br.voke.dominio.fidelidade.transacao.TipoTransacao;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transacoes_financeiras")
public class TransacaoFinanceiraJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID participanteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoTransacao tipo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, length = 255)
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    protected TransacaoFinanceiraJpa() {}

    public TransacaoFinanceiraJpa(UUID id, UUID participanteId, TipoTransacao tipo,
                                  BigDecimal valor, String descricao, LocalDateTime dataHora) {
        this.id = id;
        this.participanteId = participanteId;
        this.tipo = tipo;
        this.valor = valor;
        this.descricao = descricao;
        this.dataHora = dataHora;
    }

    public UUID getId() { return id; }
    public UUID getParticipanteId() { return participanteId; }
    public TipoTransacao getTipo() { return tipo; }
    public BigDecimal getValor() { return valor; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getDataHora() { return dataHora; }
}
