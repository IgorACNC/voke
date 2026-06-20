package br.voke.dominio.evento.notificacao;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Strategy que filtra apenas os inscritos de um lote específico.
 * Utiliza o conjunto de participantes do lote para intersectar com os elegíveis.
 */
public class SegmentacaoPorLote implements CriterioSegmentacao {

    private final int numeroLote;
    private final Set<UUID> inscritosDoLote;

    public SegmentacaoPorLote(int numeroLote, Set<UUID> inscritosDoLote) {
        Objects.requireNonNull(inscritosDoLote, "Inscritos do lote são obrigatórios");
        this.numeroLote = numeroLote;
        this.inscritosDoLote = inscritosDoLote;
    }

    @Override
    public Set<UUID> filtrar(Set<UUID> todosElegiveis) {
        return todosElegiveis.stream()
                .filter(inscritosDoLote::contains)
                .collect(Collectors.toSet());
    }

    @Override
    public String descricao() {
        return "LOTE:" + numeroLote;
    }
}
