package br.voke.infraestrutura.inscricao.carrinho;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import br.voke.dominio.inscricao.carrinho.Carrinho;
import br.voke.dominio.inscricao.carrinho.CarrinhoId;
import br.voke.dominio.inscricao.carrinho.CarrinhoRepositorio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CarrinhoRepositorioJpa implements CarrinhoRepositorio {

    private final SpringCarrinhoRepository repository;

    public CarrinhoRepositorioJpa(SpringCarrinhoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void salvar(Carrinho carrinho) {
        Optional<CarrinhoJpa> existente = repository.findById(carrinho.getId().getValor());
        if (existente.isPresent()) {
            CarrinhoJpa jpa = existente.get();
            jpa.getItens().clear();
            carrinho.getItens().forEach(item ->
                jpa.getItens().add(new ItemCarrinhoJpa(item.getEventoId(), item.getNomeEvento(),
                        item.getQuantidade(), item.getPrecoUnitario())));
            jpa.setCupomAplicado(carrinho.getCupomAplicado());
            jpa.setDescontoCupom(carrinho.getDescontoCupom());
            jpa.setCriadoEm(carrinho.getCriadoEm());
        } else {
            repository.save(CarrinhoJpaMapper.paraJpa(carrinho));
        }
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

    public void removerExpirados(LocalDateTime limite) {
        List<CarrinhoJpa> expirados = repository.buscarExpirados(limite);
        if (!expirados.isEmpty()) repository.deleteAll(expirados);
    }
}