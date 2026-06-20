package br.voke.dominio.evento.notificacao;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Strategy que filtra apenas os membros de um grupo específico do evento.
 * Utiliza o conjunto de membros do GrupoEvento para intersectar com os elegíveis.
 */
public class SegmentacaoPorGrupo implements CriterioSegmentacao {

    private final String nomeGrupo;
    private final Set<UUID> membrosDoGrupo;

    public SegmentacaoPorGrupo(String nomeGrupo, Set<UUID> membrosDoGrupo) {
        Objects.requireNonNull(nomeGrupo, "Nome do grupo é obrigatório");
        Objects.requireNonNull(membrosDoGrupo, "Membros do grupo são obrigatórios");
        this.nomeGrupo = nomeGrupo;
        this.membrosDoGrupo = membrosDoGrupo;
    }

    @Override
    public Set<UUID> filtrar(Set<UUID> todosElegiveis) {
        return todosElegiveis.stream()
                .filter(membrosDoGrupo::contains)
                .collect(Collectors.toSet());
    }

    @Override
    public String descricao() {
        return "GRUPO:" + nomeGrupo;
    }
}
