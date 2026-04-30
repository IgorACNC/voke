package br.voke.infraestrutura.evento.cupom;

import org.springframework.stereotype.Repository;
import br.voke.dominio.evento.cupom.Cupom;
import br.voke.dominio.evento.cupom.CupomId;
import br.voke.dominio.evento.cupom.CupomRepositorio;

import java.util.Optional;

@Repository
public class CupomRepositorioJpa implements CupomRepositorio {

    private final SpringCupomRepository repository;

    public CupomRepositorioJpa(SpringCupomRepository repository) {
        this.repository = repository;
    }

    public void salvar(Cupom cupom) {
        repository.save(CupomJpaMapper.paraJpa(cupom));
    }

    public Optional<Cupom> buscarPorId(CupomId id) {
        return repository.findById(id.getValor()).map(CupomJpaMapper::paraDominio);
    }

    public Optional<Cupom> buscarPorCodigo(String codigo) {
        return repository.findByCodigo(codigo).map(CupomJpaMapper::paraDominio);
    }

    public void remover(CupomId id) {
        repository.deleteById(id.getValor());
    }
}
