package br.voke.infraestrutura.fidelidade.pontos;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "contas_pontos")
public class ContaPontosJpa {

    @Id
    private UUID id;
    private UUID participanteId;
    private int saldo;

    protected ContaPontosJpa() {
    }

    public ContaPontosJpa(UUID id, UUID participanteId, int saldo) {
        this.id = id;
        this.participanteId = participanteId;
        this.saldo = saldo;
    }

    public UUID getId() { return id; }
    public UUID getParticipanteId() { return participanteId; }
    public int getSaldo() { return saldo; }
}