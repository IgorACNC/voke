package br.voke.dominio.evento.notificacao;

import java.util.Set;
import java.util.UUID;

/**
 * Strategy que não aplica nenhum filtro — todos os elegíveis recebem.
 * Comportamento padrão, retrocompatível com o envio atual.
 */
public class SegmentacaoTodos implements CriterioSegmentacao {

    @Override
    public Set<UUID> filtrar(Set<UUID> todosElegiveis) {
        return todosElegiveis;
    }

    @Override
    public String descricao() {
        return "TODOS";
    }
}
