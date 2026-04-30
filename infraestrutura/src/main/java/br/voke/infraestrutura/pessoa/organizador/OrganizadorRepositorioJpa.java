package br.voke.infraestrutura.pessoa.organizador;

import org.springframework.stereotype.Repository;
import br.voke.dominio.compartilhado.Cpf;
import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.pessoa.organizador.Organizador;
import br.voke.dominio.pessoa.organizador.OrganizadorId;
import br.voke.dominio.pessoa.organizador.OrganizadorRepositorio;

import java.util.Optional;

@Repository
public class OrganizadorRepositorioJpa implements OrganizadorRepositorio {

    private final SpringOrganizadorRepository repository;

    public OrganizadorRepositorioJpa(SpringOrganizadorRepository repository) {
        this.repository = repository;
    }

    public void salvar(Organizador organizador) {
        repository.save(OrganizadorJpaMapper.paraJpa(organizador));
    }

    public Optional<Organizador> buscarPorId(OrganizadorId id) {
        return repository.findById(id.getValor()).map(OrganizadorJpaMapper::paraDominio);
    }

    public Optional<Organizador> buscarPorEmail(Email email) {
        return repository.findByEmail(email.getValor()).map(OrganizadorJpaMapper::paraDominio);
    }

    public Optional<Organizador> buscarPorCpf(Cpf cpf) {
        return repository.findByCpf(cpf.getValor()).map(OrganizadorJpaMapper::paraDominio);
    }

    public void remover(OrganizadorId id) {
        repository.deleteById(id.getValor());
    }

    public boolean existePorEmail(Email email) {
        return repository.existsByEmail(email.getValor());
    }

    public boolean existePorCpf(Cpf cpf) {
        return repository.existsByCpf(cpf.getValor());
    }
}
