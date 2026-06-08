package br.voke.aplicacao.evento;

import br.voke.dominio.evento.categoria.CategoriaId;
import br.voke.dominio.evento.categoria.CategoriaServico;

import java.util.Objects;
import java.util.UUID;

public class EditarCategoriaCasoDeUso {

    private final CategoriaServico servico;

    public EditarCategoriaCasoDeUso(CategoriaServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID id, String novoNome) {
        servico.editar(new CategoriaId(id), novoNome);
    }
}
