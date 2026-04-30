package br.voke.dominio.fidelidade.sugestao;

import br.voke.dominio.compartilhado.EntidadeBase;

import java.util.Objects;
import java.util.UUID;

public class Sugestao extends EntidadeBase<SugestaoId> {

    private final UUID participanteId;
    private final UUID eventoId;
    private String descricao;
    private StatusSugestao status;

    public Sugestao(SugestaoId id, UUID participanteId, UUID eventoId) {
        this(id, participanteId, eventoId, null);
    }

    public Sugestao(SugestaoId id, UUID participanteId, UUID eventoId, String descricao) {
        super(id);
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        Objects.requireNonNull(eventoId, "Evento é obrigatório");
        this.participanteId = participanteId;
        this.eventoId = eventoId;
        this.descricao = descricao;
        this.status = StatusSugestao.PENDENTE;
    }

    public void editar(String novaDescricao) {
        Objects.requireNonNull(novaDescricao, "Descrição é obrigatória");
        this.descricao = novaDescricao;
    }

    public String getDescricao() { return descricao; }

    public void aprovar() {
        this.status = StatusSugestao.APROVADA;
    }

    public void rejeitar() {
        this.status = StatusSugestao.REJEITADA;
    }

    public void expirar() {
        this.status = StatusSugestao.EXPIRADA;
    }

    public boolean estaPendente() { return status == StatusSugestao.PENDENTE; }

    public UUID getParticipanteId() { return participanteId; }
    public UUID getEventoId() { return eventoId; }
    public StatusSugestao getStatus() { return status; }
}
