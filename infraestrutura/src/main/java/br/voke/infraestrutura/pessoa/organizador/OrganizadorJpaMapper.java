package br.voke.infraestrutura.pessoa.organizador;

import br.voke.dominio.compartilhado.Cpf;
import br.voke.dominio.compartilhado.DataNascimento;
import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.compartilhado.Senha;
import br.voke.dominio.pessoa.organizador.Organizador;
import br.voke.dominio.pessoa.organizador.OrganizadorId;

public final class OrganizadorJpaMapper {

    private OrganizadorJpaMapper() {
    }

    public static OrganizadorJpa paraJpa(Organizador organizador) {
        return new OrganizadorJpa(
                organizador.getId().getValor(),
                organizador.getNome().getValor(),
                organizador.getCpf().getValor(),
                organizador.getEmail().getValor(),
                organizador.getSenha().getValor(),
                organizador.getDataNascimento().getValor());
    }

    public static Organizador paraDominio(OrganizadorJpa jpa) {
        return new Organizador(
                new OrganizadorId(jpa.getId()),
                new NomeCompleto(jpa.getNome()),
                new Cpf(jpa.getCpf()),
                new Email(jpa.getEmail()),
                new Senha(jpa.getSenha()),
                new DataNascimento(jpa.getDataNascimento()));
    }
}
