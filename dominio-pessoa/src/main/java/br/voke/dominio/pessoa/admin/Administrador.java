package br.voke.dominio.pessoa.admin;

import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.compartilhado.EntidadeBase;
import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.compartilhado.Senha;

public class Administrador extends EntidadeBase<AdministradorId> {

    private NomeCompleto nome;
    private Email email;
    private Senha senha;

    public Administrador(AdministradorId id, NomeCompleto nome, Email email, Senha senha) {
        super(id);
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public void atualizarSenha(Senha novaSenha) { this.senha = novaSenha; }

    public NomeCompleto getNome() { return nome; }
    public Email getEmail() { return email; }
    public Senha getSenha() { return senha; }
}
