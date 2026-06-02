package br.voke.infraestrutura.pessoa.organizador;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "organizadores")
public class OrganizadorJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String senha;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    protected OrganizadorJpa() {
    }

    public OrganizadorJpa(UUID id, String nome, String cpf, String email, String senha, LocalDate dataNascimento) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.dataNascimento = dataNascimento;
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public LocalDate getDataNascimento() { return dataNascimento; }
}
