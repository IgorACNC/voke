package br.voke.aplicacao.evento;

import br.voke.dominio.evento.categoria.CategoriaId;
import br.voke.dominio.evento.categoria.CategoriaServico;

import java.util.Objects;
import java.util.UUID;

public class RemoverCategoriaCasoDeUso {

    private final CategoriaServico servico;

    public RemoverCategoriaCasoDeUso(CategoriaServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID id) {
        servico.remover(new CategoriaId(id));
    }
}
