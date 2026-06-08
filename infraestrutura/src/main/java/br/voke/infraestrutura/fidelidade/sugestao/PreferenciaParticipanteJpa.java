package br.voke.infraestrutura.fidelidade.sugestao;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "preferencias_participante")
public class PreferenciaParticipanteJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID participanteId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "preferencia_categorias",
            joinColumns = @JoinColumn(name = "preferencia_id"))
    @Column(name = "categoria", length = 50)
    private Set<String> categoriasPreferidas = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "preferencia_pesos_negativos",
            joinColumns = @JoinColumn(name = "preferencia_id"))
    @MapKeyColumn(name = "tag", length = 50)
    @Column(name = "peso")
    private Map<String, Integer> pesosNegativos = new HashMap<>();

    protected PreferenciaParticipanteJpa() {
    }

    public PreferenciaParticipanteJpa(UUID id, UUID participanteId,
                                      Set<String> categoriasPreferidas,
                                      Map<String, Integer> pesosNegativos) {
        this.id = id;
        this.participanteId = participanteId;
        this.categoriasPreferidas = categoriasPreferidas != null ? new HashSet<>(categoriasPreferidas) : new HashSet<>();
        this.pesosNegativos = pesosNegativos != null ? new HashMap<>(pesosNegativos) : new HashMap<>();
    }

    public UUID getId() { return id; }
    public UUID getParticipanteId() { return participanteId; }
    public Set<String> getCategoriasPreferidas() { return categoriasPreferidas; }
    public Map<String, Integer> getPesosNegativos() { return pesosNegativos; }
}
