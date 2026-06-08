package br.voke.dominio.pessoa.admin;

import br.voke.dominio.compartilhado.Email;

import java.util.Optional;

public interface AdministradorRepositorio {
    void salvar(Administrador administrador);
    Optional<Administrador> buscarPorEmail(Email email);
    boolean existePorEmail(Email email);
}
