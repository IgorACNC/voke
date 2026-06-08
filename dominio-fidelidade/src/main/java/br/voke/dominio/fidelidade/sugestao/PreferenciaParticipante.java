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
    private Set<String> categoriasPreferidas;
    private Map<String, Integer> pesosNegativos;

    public PreferenciaParticipante(PreferenciaParticipanteId id, UUID participanteId) {
        this(id, participanteId, new HashSet<>(), new HashMap<>());
    }

    public PreferenciaParticipante(PreferenciaParticipanteId id, UUID participanteId,
                                   Set<String> categoriasPreferidas, Map<String, Integer> pesosNegativos) {
        super(id);
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        this.participanteId = participanteId;
        this.categoriasPreferidas = new HashSet<>(categoriasPreferidas);
        this.pesosNegativos = new HashMap<>(pesosNegativos);
    }

    public void definirCategorias(Set<String> categorias) {
        Objects.requireNonNull(categorias, "Categorias são obrigatórias");
        this.categoriasPreferidas = new HashSet<>();
        categorias.forEach(c -> this.categoriasPreferidas.add(normalizar(c)));
    }

    public void registrarFeedbackNegativo(Set<String> tagsRejeitadas) {
        Objects.requireNonNull(tagsRejeitadas, "Tags são obrigatórias");
        tagsRejeitadas.forEach(tag -> {
            String chave = normalizar(tag);
            pesosNegativos.merge(chave, PESO_NEGATIVO_INCREMENTO, Integer::sum);
        });
    }

    public int calcularScore(Set<String> tagsEvento) {
        if (tagsEvento == null || tagsEvento.isEmpty()) return 0;
        int score = 0;
        for (String tag : tagsEvento) {
            String chave = normalizar(tag);
            if (categoriasPreferidas.contains(chave)) score += 10;
            score -= pesosNegativos.getOrDefault(chave, 0) * 5;
        }
        return score;
    }

    public boolean deveDescartar(Set<String> tagsEvento) {
        if (tagsEvento == null || tagsEvento.isEmpty()) return false;
        return tagsEvento.stream()
                .map(this::normalizar)
                .anyMatch(t -> pesosNegativos.getOrDefault(t, 0) >= PESO_NEGATIVO_LIMITE_DESCARTE);
    }

    private String normalizar(String valor) {
        return valor.trim().toLowerCase();
    }

    public UUID getParticipanteId() { return participanteId; }
    public Set<String> getCategoriasPreferidas() { return Set.copyOf(categoriasPreferidas); }
    public Map<String, Integer> getPesosNegativos() { return Map.copyOf(pesosNegativos); }
}
