package br.voke.dominio.evento.categoria;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepositorio {
    void salvar(Categoria categoria);
    Optional<Categoria> buscarPorId(CategoriaId id);
    Optional<Categoria> buscarPorNome(String nome);
    List<Categoria> listarTodas();
    void remover(CategoriaId id);
    boolean existePorNome(String nome);
    boolean existePorNomeExcetoId(String nome, CategoriaId id);
}
