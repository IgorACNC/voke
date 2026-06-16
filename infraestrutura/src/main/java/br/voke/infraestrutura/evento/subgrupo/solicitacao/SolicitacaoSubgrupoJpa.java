package br.voke.infraestrutura.evento.subgrupo.solicitacao;

import br.voke.dominio.evento.subgrupo.solicitacao.StatusSolicitacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "solicitacoes_subgrupo", indexes = {
        @Index(name = "idx_solicitacao_subgrupo_status",
                columnList = "subgrupoId,participanteId,status")
})
public class SolicitacaoSubgrupoJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID subgrupoId;

    @Column(nullable = false)
    private UUID participanteId;

    @Column(columnDefinition = "TEXT")
    private String mensagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusSolicitacao status;

    @Column(nullable = false)
    private LocalDateTime dataSolicitacao;

    @Column
    private LocalDateTime dataDecisao;

    @Column
    private UUID decididoPor;

    protected SolicitacaoSubgrupoJpa() {
    }

    public SolicitacaoSubgrupoJpa(UUID id, UUID subgrupoId, UUID participanteId, String mensagem,
                                   StatusSolicitacao status, LocalDateTime dataSolicitacao,
                                   LocalDateTime dataDecisao, UUID decididoPor) {
        this.id = id;
        this.subgrupoId = subgrupoId;
        this.participanteId = participanteId;
        this.mensagem = mensagem;
        this.status = status;
        this.dataSolicitacao = dataSolicitacao;
        this.dataDecisao = dataDecisao;
        this.decididoPor = decididoPor;
    }

    public UUID getId() { return id; }
    public UUID getSubgrupoId() { return subgrupoId; }
    public UUID getParticipanteId() { return participanteId; }
    public String getMensagem() { return mensagem; }
    public StatusSolicitacao getStatus() { return status; }
    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public LocalDateTime getDataDecisao() { return dataDecisao; }
    public UUID getDecididoPor() { return decididoPor; }
}
