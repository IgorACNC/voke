package br.voke.dominio.pessoa.participante;

import java.util.Optional;

public interface TokenRecuperacaoSenhaRepositorio {
    void salvar(TokenRecuperacaoSenha token);
    Optional<TokenRecuperacaoSenha> buscarPorToken(String token);
}
