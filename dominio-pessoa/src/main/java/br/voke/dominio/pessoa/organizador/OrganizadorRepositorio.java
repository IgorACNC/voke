package br.voke.dominio.pessoa.organizador;

import br.voke.dominio.compartilhado.Cpf;
import br.voke.dominio.compartilhado.Email;

import java.util.Optional;

public interface OrganizadorRepositorio {
    void salvar(Organizador organizador);
    Optional<Organizador> buscarPorId(OrganizadorId id);
    Optional<Organizador> buscarPorEmail(Email email);
    Optional<Organizador> buscarPorCpf(Cpf cpf);
    void remover(OrganizadorId id);
    boolean existePorEmail(Email email);
    boolean existePorCpf(Cpf cpf);
}
