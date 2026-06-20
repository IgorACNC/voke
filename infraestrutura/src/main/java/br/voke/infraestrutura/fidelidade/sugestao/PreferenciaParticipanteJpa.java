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
    @Column(name = "categoria_id")
    private Set<UUID> categoriaIds = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "preferencia_pesos_negativos",
            joinColumns = @JoinColumn(name = "preferencia_id"))
    @MapKeyColumn(name = "categoria_id")
    @Column(name = "peso")
    private Map<UUID, Integer> pesosNegativos = new HashMap<>();

    protected PreferenciaParticipanteJpa() {
    }

    public PreferenciaParticipanteJpa(UUID id, UUID participanteId,
                                      Set<UUID> categoriaIds,
                                      Map<UUID, Integer> pesosNegativos) {
        this.id = id;
        this.participanteId = participanteId;
        this.categoriaIds = categoriaIds != null ? new HashSet<>(categoriaIds) : new HashSet<>();
        this.pesosNegativos = pesosNegativos != null ? new HashMap<>(pesosNegativos) : new HashMap<>();
    }

    public UUID getId() { return id; }
    public UUID getParticipanteId() { return participanteId; }
    public Set<UUID> getCategoriaIds() { return categoriaIds; }
    public Map<UUID, Integer> getPesosNegativos() { return pesosNegativos; }
}
