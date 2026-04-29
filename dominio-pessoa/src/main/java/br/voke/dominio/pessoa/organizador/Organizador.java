package br.voke.dominio.pessoa.organizador;

import br.voke.dominio.compartilhado.Cpf;
import br.voke.dominio.compartilhado.DataNascimento;
import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.compartilhado.EntidadeBase;
import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.compartilhado.Senha;
import br.voke.dominio.pessoa.excecao.DataNascimentoImutavelException;
import br.voke.dominio.pessoa.excecao.MaioridadeObrigatoriaException;

public class Organizador extends EntidadeBase<OrganizadorId> {

    private static final int IDADE_MINIMA = 18;

    private NomeCompleto nome;
    private final Cpf cpf;
    private Email email;
    private Senha senha;
    private final DataNascimento dataNascimento;

    public Organizador(OrganizadorId id, NomeCompleto nome, Cpf cpf, Email email,
                       Senha senha, DataNascimento dataNascimento) {
        super(id);
        if (dataNascimento.idadeEmAnos() < IDADE_MINIMA) {
            throw new MaioridadeObrigatoriaException();
        }
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.dataNascimento = dataNascimento;
    }

    public void atualizarNome(NomeCompleto novoNome) {
        this.nome = novoNome;
    }

    public void atualizarEmail(Email novoEmail) {
        this.email = novoEmail;
    }

    public void atualizarSenha(Senha novaSenha) {
        this.senha = novaSenha;
    }

    public void alterarDataNascimento(DataNascimento novaData) {
        throw new DataNascimentoImutavelException();
    }

    public NomeCompleto getNome() { return nome; }
    public Cpf getCpf() { return cpf; }
    public Email getEmail() { return email; }
    public Senha getSenha() { return senha; }
    public DataNascimento getDataNascimento() { return dataNascimento; }
    public int getIdade() { return dataNascimento.idadeEmAnos(); }
}
