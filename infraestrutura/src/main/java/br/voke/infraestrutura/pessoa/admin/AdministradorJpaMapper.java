package br.voke.infraestrutura.pessoa.admin;

import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.compartilhado.Senha;
import br.voke.dominio.pessoa.admin.Administrador;
import br.voke.dominio.pessoa.admin.AdministradorId;

public final class AdministradorJpaMapper {

    private AdministradorJpaMapper() {}

    public static AdministradorJpa paraJpa(Administrador a) {
        return new AdministradorJpa(
                a.getId().getValor(),
                a.getNome().getValor(),
                a.getEmail().getValor(),
                a.getSenha().getValor());
    }

    public static Administrador paraDominio(AdministradorJpa jpa) {
        return new Administrador(
                new AdministradorId(jpa.getId()),
                new NomeCompleto(jpa.getNome()),
                new Email(jpa.getEmail()),
                new Senha(jpa.getSenha()));
    }
}
