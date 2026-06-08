package br.voke.infraestrutura.evento.categoria;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "categorias")
public class CategoriaJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String nome;

    protected CategoriaJpa() {}

    public CategoriaJpa(UUID id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
}
