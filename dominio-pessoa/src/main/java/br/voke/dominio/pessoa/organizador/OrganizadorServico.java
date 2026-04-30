package br.voke.dominio.pessoa.organizador;

import br.voke.dominio.compartilhado.Cpf;
import br.voke.dominio.compartilhado.DataNascimento;
import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.compartilhado.Senha;
import br.voke.dominio.pessoa.excecao.CpfDuplicadoException;
import br.voke.dominio.pessoa.excecao.EmailDuplicadoException;

import java.util.Objects;

public class OrganizadorServico {

    private final OrganizadorRepositorio repositorio;

    public OrganizadorServico(OrganizadorRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
    }

    public Organizador cadastrar(NomeCompleto nome, Cpf cpf, Email email,
                                 Senha senha, DataNascimento dataNascimento) {
        if (repositorio.existePorCpf(cpf)) {
            throw new CpfDuplicadoException();
        }
        if (repositorio.existePorEmail(email)) {
            throw new EmailDuplicadoException();
        }
        Organizador organizador = new Organizador(
                OrganizadorId.novo(), nome, cpf, email, senha, dataNascimento
        );
        repositorio.salvar(organizador);
        return organizador;
    }

    public void atualizarDados(OrganizadorId id, NomeCompleto novoNome, Email novoEmail) {
        Organizador organizador = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Organizador não encontrado"));
        if (!organizador.getEmail().equals(novoEmail) && repositorio.existePorEmail(novoEmail)) {
            throw new EmailDuplicadoException();
        }
        organizador.atualizarNome(novoNome);
        organizador.atualizarEmail(novoEmail);
        repositorio.salvar(organizador);
    }

    public void remover(OrganizadorId id) {
        repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Organizador não encontrado"));
        repositorio.remover(id);
    }
}
