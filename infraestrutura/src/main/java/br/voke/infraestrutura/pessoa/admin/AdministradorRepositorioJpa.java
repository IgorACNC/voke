package br.voke.infraestrutura.pessoa.admin;

import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.pessoa.admin.Administrador;
import br.voke.dominio.pessoa.admin.AdministradorRepositorio;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AdministradorRepositorioJpa implements AdministradorRepositorio {

    private final SpringAdministradorRepository repository;

    public AdministradorRepositorioJpa(SpringAdministradorRepository repository) {
        this.repository = repository;
    }

    @Override
    public void salvar(Administrador administrador) {
        repository.save(AdministradorJpaMapper.paraJpa(administrador));
    }

    @Override
    public Optional<Administrador> buscarPorEmail(Email email) {
        return repository.findByEmail(email.getValor()).map(AdministradorJpaMapper::paraDominio);
    }

    @Override
    public boolean existePorEmail(Email email) {
        return repository.existsByEmail(email.getValor());
    }
}
