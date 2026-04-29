package br.voke.infraestrutura.pessoa.parceiro;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import br.voke.dominio.pessoa.parceiro.AtividadeParceiro;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "parceiros")
public class ParceiroJpa {

    @Id
    private UUID id;
    private UUID participanteId;
    private UUID organizadorId;
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<AtividadeParceiro> atividades = new HashSet<>();

    protected ParceiroJpa() {
    }

    public ParceiroJpa(UUID id, UUID participanteId, UUID organizadorId, Set<AtividadeParceiro> atividades) {
        this.id = id;
        this.participanteId = participanteId;
        this.organizadorId = organizadorId;
        this.atividades = new HashSet<>(atividades);
    }

    public UUID getId() { return id; }
    public UUID getParticipanteId() { return participanteId; }
    public UUID getOrganizadorId() { return organizadorId; }
    public Set<AtividadeParceiro> getAtividades() { return atividades; }
}
