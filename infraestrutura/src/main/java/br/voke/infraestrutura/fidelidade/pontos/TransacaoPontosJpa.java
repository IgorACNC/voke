package br.voke.infraestrutura.fidelidade.pontos;

import br.voke.dominio.fidelidade.pontos.TipoTransacaoPontos;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transacoes_pontos")
public class TransacaoPontosJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID participanteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoTransacaoPontos tipo;

    @Column(nullable = false)
    private int pontos;

    @Column(nullable = false, length = 255)
    private String descricao;

    @Column(nullable = true)
    private UUID referenciaId;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    protected TransacaoPontosJpa() {}

    public TransacaoPontosJpa(UUID id, UUID participanteId, TipoTransacaoPontos tipo,
                              int pontos, String descricao, UUID referenciaId, LocalDateTime dataHora) {
        this.id = id;
        this.participanteId = participanteId;
        this.tipo = tipo;
        this.pontos = pontos;
        this.descricao = descricao;
        this.referenciaId = referenciaId;
        this.dataHora = dataHora;
    }

    public UUID getId() { return id; }
    public UUID getParticipanteId() { return participanteId; }
    public TipoTransacaoPontos getTipo() { return tipo; }
    public int getPontos() { return pontos; }
    public String getDescricao() { return descricao; }
    public UUID getReferenciaId() { return referenciaId; }
    public LocalDateTime getDataHora() { return dataHora; }
}
