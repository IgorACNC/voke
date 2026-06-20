package br.voke.dominio.fidelidade.pontos;

import br.voke.dominio.compartilhado.EntidadeBase;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class TransacaoPontos extends EntidadeBase<TransacaoPontosId> {

    private final UUID participanteId;
    private final TipoTransacaoPontos tipo;
    private final int pontos;
    private final String descricao;
    private final UUID referenciaId; // eventoId, recompensaId etc.
    private final LocalDateTime dataHora;

    public TransacaoPontos(TransacaoPontosId id, UUID participanteId, TipoTransacaoPontos tipo,
                           int pontos, String descricao, UUID referenciaId) {
        super(id);
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        Objects.requireNonNull(tipo, "Tipo é obrigatório");
        if (pontos <= 0) throw new IllegalArgumentException("Pontos devem ser positivos");
        this.participanteId = participanteId;
        this.tipo = tipo;
        this.pontos = pontos;
        this.descricao = descricao == null ? "" : descricao;
        this.referenciaId = referenciaId;
        this.dataHora = LocalDateTime.now();
    }

    public UUID getParticipanteId() { return participanteId; }
    public TipoTransacaoPontos getTipo() { return tipo; }
    public int getPontos() { return pontos; }
    public String getDescricao() { return descricao; }
    public UUID getReferenciaId() { return referenciaId; }
    public LocalDateTime getDataHora() { return dataHora; }

    public String getDirecao() {
        return tipo == TipoTransacaoPontos.GANHO_PRESENCA ? "ENTRADA" : "SAIDA";
    }
}
