package br.voke.infraestrutura.pessoa.amizade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import br.voke.dominio.pessoa.amizade.StatusAmizade;

import java.util.UUID;

@Entity
@Table(name = "amizades")
public class AmizadeJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID solicitanteId;

    @Column(nullable = false)
    private UUID receptorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusAmizade status;

    protected AmizadeJpa() {
    }

    public AmizadeJpa(UUID id, UUID solicitanteId, UUID receptorId, StatusAmizade status) {
        this.id = id;
        this.solicitanteId = solicitanteId;
        this.receptorId = receptorId;
        this.status = status;
    }

    public UUID getId() { return id; }
    public UUID getSolicitanteId() { return solicitanteId; }
    public UUID getReceptorId() { return receptorId; }
    public StatusAmizade getStatus() { return status; }
}
