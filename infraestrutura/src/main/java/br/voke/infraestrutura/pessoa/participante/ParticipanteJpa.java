package br.voke.infraestrutura.pessoa.participante;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "participantes")
public class ParticipanteJpa {

    @Id
    private UUID id;
    private String nome;
    private String cpf;
    private String email;
    private String senha;
    private LocalDate dataNascimento;

    protected ParticipanteJpa() {
    }

    public ParticipanteJpa(UUID id, String nome, String cpf, String email, String senha, LocalDate dataNascimento) {
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
