package br.voke.aplicacao.evento;

import br.voke.dominio.evento.categoria.Categoria;
import br.voke.dominio.evento.categoria.CategoriaServico;

import java.util.List;
import java.util.Objects;

public class ListarCategoriasCasoDeUso {

    private final CategoriaServico servico;

    public ListarCategoriasCasoDeUso(CategoriaServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public List<Categoria> executar() {
        return servico.listarTodas();
    }
}
