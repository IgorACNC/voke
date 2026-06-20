package br.voke.dominio.evento.subgrupo.solicitacao;

import br.voke.dominio.compartilhado.EntidadeBase;
import br.voke.dominio.evento.subgrupo.SolicitacaoJaDecididaException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class SolicitacaoSubgrupo extends EntidadeBase<SolicitacaoSubgrupoId> {

    private final UUID subgrupoId;
    private final UUID participanteId;
    private final String mensagem;
    private StatusSolicitacao status;
    private final LocalDateTime dataSolicitacao;
    private LocalDateTime dataDecisao;
    private UUID decididoPor;

    public SolicitacaoSubgrupo(SolicitacaoSubgrupoId id, UUID subgrupoId, UUID participanteId,
                                String mensagem) {
        super(id);
        Objects.requireNonNull(subgrupoId, "Subgrupo é obrigatório");
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        this.subgrupoId = subgrupoId;
        this.participanteId = participanteId;
        this.mensagem = mensagem;
        this.status = StatusSolicitacao.PENDENTE;
        this.dataSolicitacao = LocalDateTime.now();
        this.dataDecisao = null;
        this.decididoPor = null;
    }

    public SolicitacaoSubgrupo(SolicitacaoSubgrupoId id, UUID subgrupoId, UUID participanteId,
                                String mensagem, StatusSolicitacao status,
                                LocalDateTime dataSolicitacao, LocalDateTime dataDecisao,
                                UUID decididoPor) {
        super(id);
        this.subgrupoId = subgrupoId;
        this.participanteId = participanteId;
        this.mensagem = mensagem;
        this.status = status;
        this.dataSolicitacao = dataSolicitacao;
        this.dataDecisao = dataDecisao;
        this.decididoPor = decididoPor;
    }

    public void aprovar(UUID decididoPor) {
        garantirPendente();
        Objects.requireNonNull(decididoPor, "Decididor é obrigatório");
        this.status = StatusSolicitacao.APROVADA;
        this.dataDecisao = LocalDateTime.now();
        this.decididoPor = decididoPor;
    }

    public void rejeitar(UUID decididoPor) {
        garantirPendente();
        Objects.requireNonNull(decididoPor, "Decididor é obrigatório");
        this.status = StatusSolicitacao.REJEITADA;
        this.dataDecisao = LocalDateTime.now();
        this.decididoPor = decididoPor;
    }

    private void garantirPendente() {
        if (status != StatusSolicitacao.PENDENTE) {
            throw new SolicitacaoJaDecididaException();
        }
    }

    public boolean estaPendente() { return status == StatusSolicitacao.PENDENTE; }

    public UUID getSubgrupoId() { return subgrupoId; }
    public UUID getParticipanteId() { return participanteId; }
    public String getMensagem() { return mensagem; }
    public StatusSolicitacao getStatus() { return status; }
    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public LocalDateTime getDataDecisao() { return dataDecisao; }
    public UUID getDecididoPor() { return decididoPor; }
}
