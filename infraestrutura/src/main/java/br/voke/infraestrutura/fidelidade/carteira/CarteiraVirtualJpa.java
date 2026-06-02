package br.voke.infraestrutura.fidelidade.carteira;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "carteiras_virtuais")
public class CarteiraVirtualJpa {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID participanteId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal saldo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalInseridoHoje;

    protected CarteiraVirtualJpa() {
    }

    public CarteiraVirtualJpa(UUID id, UUID participanteId, BigDecimal saldo, BigDecimal totalInseridoHoje) {
        this.id = id;
        this.participanteId = participanteId;
        this.saldo = saldo;
        this.totalInseridoHoje = totalInseridoHoje;
    }

    public UUID getId() { return id; }
    public UUID getParticipanteId() { return participanteId; }
    public BigDecimal getSaldo() { return saldo; }
    public BigDecimal getTotalInseridoHoje() { return totalInseridoHoje; }
}
