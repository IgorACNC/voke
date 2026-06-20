package br.voke.dominio.pessoa.participante;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class TokenRecuperacaoSenha {

    private static final int VALIDADE_EM_MINUTOS = 30;

    private final UUID id;
    private final String token;
    private final UUID participanteId;
    private final LocalDateTime expiraEm;
    private boolean usado;

    public TokenRecuperacaoSenha(UUID id, String token, UUID participanteId,
                                  LocalDateTime expiraEm, boolean usado) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(token);
        Objects.requireNonNull(participanteId);
        Objects.requireNonNull(expiraEm);
        this.id = id;
        this.token = token;
        this.participanteId = participanteId;
        this.expiraEm = expiraEm;
        this.usado = usado;
    }

    public static TokenRecuperacaoSenha gerarPara(UUID participanteId) {
        return new TokenRecuperacaoSenha(
                UUID.randomUUID(),
                UUID.randomUUID().toString().replace("-", ""),
                participanteId,
                LocalDateTime.now().plusMinutes(VALIDADE_EM_MINUTOS),
                false);
    }

    public boolean estaValido() {
        return !usado && LocalDateTime.now().isBefore(expiraEm);
    }

    public void marcarComoUsado() {
        this.usado = true;
    }

    public UUID getId() { return id; }
    public String getToken() { return token; }
    public UUID getParticipanteId() { return participanteId; }
    public LocalDateTime getExpiraEm() { return expiraEm; }
    public boolean isUsado() { return usado; }
}
