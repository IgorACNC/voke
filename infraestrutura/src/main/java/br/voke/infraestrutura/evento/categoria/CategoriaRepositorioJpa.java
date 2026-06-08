package br.voke.infraestrutura.evento.categoria;

import br.voke.dominio.evento.categoria.Categoria;
import br.voke.dominio.evento.categoria.CategoriaId;
import br.voke.dominio.evento.categoria.CategoriaRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoriaRepositorioJpa implements CategoriaRepositorio {

    private final SpringCategoriaRepository repository;

    public CategoriaRepositorioJpa(SpringCategoriaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void salvar(Categoria categoria) {
        repository.save(CategoriaJpaMapper.paraJpa(categoria));
    }

    @Override
    public Optional<Categoria> buscarPorId(CategoriaId id) {
        return repository.findById(id.getValor()).map(CategoriaJpaMapper::paraDominio);
    }

    @Override
    public Optional<Categoria> buscarPorNome(String nome) {
        return repository.findByNomeIgnoreCase(nome).map(CategoriaJpaMapper::paraDominio);
    }

    @Override
    public List<Categoria> listarTodas() {
        return repository.findAll().stream().map(CategoriaJpaMapper::paraDominio).toList();
    }

    @Override
    public void remover(CategoriaId id) {
        repository.deleteById(id.getValor());
    }

    @Override
    public boolean existePorNome(String nome) {
        return repository.existsByNomeIgnoreCase(nome);
    }

    @Override
    public boolean existePorNomeExcetoId(String nome, CategoriaId id) {
        return repository.existsByNomeIgnoreCaseAndIdNot(nome, id.getValor());
    }
}
