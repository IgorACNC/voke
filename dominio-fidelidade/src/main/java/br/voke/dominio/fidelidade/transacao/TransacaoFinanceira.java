package br.voke.dominio.fidelidade.transacao;

import br.voke.dominio.compartilhado.EntidadeBase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class TransacaoFinanceira extends EntidadeBase<TransacaoFinanceiraId> {

    private final UUID participanteId;
    private final TipoTransacao tipo;
    private final BigDecimal valor;
    private final String descricao;
    private final LocalDateTime dataHora;

    public TransacaoFinanceira(TransacaoFinanceiraId id, UUID participanteId,
                               TipoTransacao tipo, BigDecimal valor, String descricao) {
        super(id);
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        Objects.requireNonNull(tipo, "Tipo é obrigatório");
        Objects.requireNonNull(valor, "Valor é obrigatório");
        Objects.requireNonNull(descricao, "Descrição é obrigatória");
        this.participanteId = participanteId;
        this.tipo = tipo;
        this.valor = valor;
        this.descricao = descricao;
        this.dataHora = LocalDateTime.now();
    }

    public UUID getParticipanteId() { return participanteId; }
    public TipoTransacao getTipo() { return tipo; }
    public BigDecimal getValor() { return valor; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getDataHora() { return dataHora; }
}
