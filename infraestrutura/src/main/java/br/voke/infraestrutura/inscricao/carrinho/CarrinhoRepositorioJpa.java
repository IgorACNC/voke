package br.voke.infraestrutura.inscricao.carrinho;

import org.springframework.stereotype.Repository;
import br.voke.dominio.inscricao.carrinho.Carrinho;
import br.voke.dominio.inscricao.carrinho.CarrinhoId;
import br.voke.dominio.inscricao.carrinho.CarrinhoRepositorio;

import java.util.Optional;
import java.util.UUID;

@Repository
public class CarrinhoRepositorioJpa implements CarrinhoRepositorio {

    private final SpringCarrinhoRepository repository;

    public CarrinhoRepositorioJpa(SpringCarrinhoRepository repository) {
        this.repository = repository;
    }

    public void salvar(Carrinho carrinho) {
        repository.save(CarrinhoJpaMapper.paraJpa(carrinho));
    }

    public Optional<Carrinho> buscarPorId(CarrinhoId id) {
        return repository.findById(id.getValor()).map(CarrinhoJpaMapper::paraDominio);
    }

    public Optional<Carrinho> buscarPorParticipanteId(UUID participanteId) {
        return repository.findByParticipanteId(participanteId).map(CarrinhoJpaMapper::paraDominio);
    }

    public void remover(CarrinhoId id) {
        repository.deleteById(id.getValor());
    }
}