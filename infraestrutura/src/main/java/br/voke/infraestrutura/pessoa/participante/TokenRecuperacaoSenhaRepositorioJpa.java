package br.voke.infraestrutura.pessoa.participante;

import br.voke.dominio.pessoa.participante.TokenRecuperacaoSenha;
import br.voke.dominio.pessoa.participante.TokenRecuperacaoSenhaRepositorio;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TokenRecuperacaoSenhaRepositorioJpa implements TokenRecuperacaoSenhaRepositorio {

    private final SpringTokenRecuperacaoSenhaRepository repository;

    public TokenRecuperacaoSenhaRepositorioJpa(SpringTokenRecuperacaoSenhaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void salvar(TokenRecuperacaoSenha token) {
        repository.save(TokenRecuperacaoSenhaJpaMapper.paraJpa(token));
    }

    @Override
    public Optional<TokenRecuperacaoSenha> buscarPorToken(String token) {
        return repository.findByToken(token).map(TokenRecuperacaoSenhaJpaMapper::paraDominio);
    }
}
