package br.voke.aplicacao.evento;

import br.voke.dominio.evento.categoria.Categoria;
import br.voke.dominio.evento.categoria.CategoriaServico;

import java.util.Objects;

public class CadastrarCategoriaCasoDeUso {

    private final CategoriaServico servico;

    public CadastrarCategoriaCasoDeUso(CategoriaServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public Categoria executar(String nome) {
        return servico.cadastrar(nome);
    }
}
