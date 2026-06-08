package br.voke.dominio.fidelidade.sugestao;

import br.voke.dominio.compartilhado.EntidadeBase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class PreferenciaParticipante extends EntidadeBase<PreferenciaParticipanteId> {

    private static final int PESO_NEGATIVO_INCREMENTO = 1;
    private static final int PESO_NEGATIVO_LIMITE_DESCARTE = 3;

    private final UUID participanteId;
    private Set<UUID> categoriaIds;
    private Map<UUID, Integer> pesosNegativos;

    public PreferenciaParticipante(PreferenciaParticipanteId id, UUID participanteId) {
        this(id, participanteId, new HashSet<>(), new HashMap<>());
    }

    public PreferenciaParticipante(PreferenciaParticipanteId id, UUID participanteId,
                                   Set<UUID> categoriaIds, Map<UUID, Integer> pesosNegativos) {
        super(id);
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        this.participanteId = participanteId;
        this.categoriaIds = new HashSet<>(categoriaIds);
        this.pesosNegativos = new HashMap<>(pesosNegativos);
    }

    public void definirCategorias(Set<UUID> categorias) {
        Objects.requireNonNull(categorias, "Categorias são obrigatórias");
        this.categoriaIds = new HashSet<>(categorias);
    }

    public void registrarFeedbackNegativo(Set<UUID> categoriasRejeitadas) {
        Objects.requireNonNull(categoriasRejeitadas, "Categorias são obrigatórias");
        categoriasRejeitadas.forEach(c ->
                pesosNegativos.merge(c, PESO_NEGATIVO_INCREMENTO, Integer::sum));
    }

    public int calcularScore(Set<UUID> categoriasEvento) {
        if (categoriasEvento == null || categoriasEvento.isEmpty()) return 0;
        int score = 0;
        for (UUID c : categoriasEvento) {
            if (categoriaIds.contains(c)) score += 10;
            score -= pesosNegativos.getOrDefault(c, 0) * 5;
        }
        return score;
    }

    public boolean deveDescartar(Set<UUID> categoriasEvento) {
        if (categoriasEvento == null || categoriasEvento.isEmpty()) return false;
        return categoriasEvento.stream()
                .anyMatch(c -> pesosNegativos.getOrDefault(c, 0) >= PESO_NEGATIVO_LIMITE_DESCARTE);
    }

    public UUID getParticipanteId() { return participanteId; }
    public Set<UUID> getCategoriaIds() { return Set.copyOf(categoriaIds); }
    public Map<UUID, Integer> getPesosNegativos() { return Map.copyOf(pesosNegativos); }
}
