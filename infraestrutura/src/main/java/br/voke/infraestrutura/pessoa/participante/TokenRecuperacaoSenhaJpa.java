package br.voke.infraestrutura.pessoa.participante;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tokens_recuperacao_senha")
public class TokenRecuperacaoSenhaJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @Column(nullable = false)
    private UUID participanteId;

    @Column(nullable = false)
    private LocalDateTime expiraEm;

    @Column(nullable = false)
    private boolean usado;

    protected TokenRecuperacaoSenhaJpa() {}

    public TokenRecuperacaoSenhaJpa(UUID id, String token, UUID participanteId,
                                     LocalDateTime expiraEm, boolean usado) {
        this.id = id;
        this.token = token;
        this.participanteId = participanteId;
        this.expiraEm = expiraEm;
        this.usado = usado;
    }

    public UUID getId() { return id; }
    public String getToken() { return token; }
    public UUID getParticipanteId() { return participanteId; }
    public LocalDateTime getExpiraEm() { return expiraEm; }
    public boolean isUsado() { return usado; }
}
