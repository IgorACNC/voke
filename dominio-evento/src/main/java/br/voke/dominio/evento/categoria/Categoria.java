package br.voke.dominio.evento.categoria;

import br.voke.dominio.compartilhado.EntidadeBase;

import java.util.Objects;

public class Categoria extends EntidadeBase<CategoriaId> {

    private static final int TAMANHO_MIN = 2;
    private static final int TAMANHO_MAX = 50;

    private String nome;

    public Categoria(CategoriaId id, String nome) {
        super(id);
        atualizarNome(nome);
    }

    public final void atualizarNome(String novoNome) {
        Objects.requireNonNull(novoNome, "Nome da categoria é obrigatório");
        String tratado = novoNome.trim();
        if (tratado.length() < TAMANHO_MIN || tratado.length() > TAMANHO_MAX) {
            throw new IllegalArgumentException(
                    "Nome da categoria deve ter entre " + TAMANHO_MIN + " e " + TAMANHO_MAX + " caracteres");
        }
        this.nome = normalizar(tratado);
    }

    private String normalizar(String valor) {
        return Character.toUpperCase(valor.charAt(0)) + valor.substring(1).toLowerCase();
    }

    public String getNome() { return nome; }
}
