package br.voke.dominio.evento.categoria;

import java.util.List;
import java.util.Objects;

public class CategoriaServico {

    private final CategoriaRepositorio repositorio;

    public CategoriaServico(CategoriaRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
    }

    public Categoria cadastrar(String nome) {
        Categoria categoria = new Categoria(CategoriaId.novo(), nome);
        if (repositorio.existePorNome(categoria.getNome())) {
            throw new IllegalArgumentException("Já existe uma categoria com esse nome");
        }
        repositorio.salvar(categoria);
        return categoria;
    }

    public void editar(CategoriaId id, String novoNome) {
        Categoria categoria = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
        categoria.atualizarNome(novoNome);
        if (repositorio.existePorNomeExcetoId(categoria.getNome(), id)) {
            throw new IllegalArgumentException("Já existe outra categoria com esse nome");
        }
        repositorio.salvar(categoria);
    }

    public void remover(CategoriaId id) {
        repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
        repositorio.remover(id);
    }

    public List<Categoria> listarTodas() {
        return repositorio.listarTodas();
    }
}
