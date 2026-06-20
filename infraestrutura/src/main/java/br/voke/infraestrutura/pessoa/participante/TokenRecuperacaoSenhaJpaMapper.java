package br.voke.infraestrutura.pessoa.participante;

import br.voke.dominio.pessoa.participante.TokenRecuperacaoSenha;

public final class TokenRecuperacaoSenhaJpaMapper {

    private TokenRecuperacaoSenhaJpaMapper() {}

    public static TokenRecuperacaoSenhaJpa paraJpa(TokenRecuperacaoSenha token) {
        return new TokenRecuperacaoSenhaJpa(
                token.getId(), token.getToken(), token.getParticipanteId(),
                token.getExpiraEm(), token.isUsado());
    }

    public static TokenRecuperacaoSenha paraDominio(TokenRecuperacaoSenhaJpa jpa) {
        return new TokenRecuperacaoSenha(
                jpa.getId(), jpa.getToken(), jpa.getParticipanteId(),
                jpa.getExpiraEm(), jpa.isUsado());
    }
}
