package br.voke.infraestrutura.fidelidade.carteira;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Column(name = "saldo_promocional", nullable = false, precision = 10, scale = 2,
            columnDefinition = "DECIMAL(10,2) NOT NULL DEFAULT 0")
    private BigDecimal saldoPromocional;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalInseridoHoje;

    @Column(nullable = false)
    private int contadorSaquesHoje;

    @Column
    private LocalDate dataContador;

    protected CarteiraVirtualJpa() {}

    public CarteiraVirtualJpa(UUID id, UUID participanteId, BigDecimal saldo,
                              BigDecimal saldoPromocional,
                              BigDecimal totalInseridoHoje, int contadorSaquesHoje, LocalDate dataContador) {
        this.id = id;
        this.participanteId = participanteId;
        this.saldo = saldo;
        this.saldoPromocional = saldoPromocional != null ? saldoPromocional : java.math.BigDecimal.ZERO;
        this.totalInseridoHoje = totalInseridoHoje;
        this.contadorSaquesHoje = contadorSaquesHoje;
        this.dataContador = dataContador;
    }

    public UUID getId() { return id; }
    public UUID getParticipanteId() { return participanteId; }
    public BigDecimal getSaldo() { return saldo; }
    public BigDecimal getSaldoPromocional() {
        return saldoPromocional != null ? saldoPromocional : java.math.BigDecimal.ZERO;
    }
    public BigDecimal getTotalInseridoHoje() { return totalInseridoHoje; }
    public int getContadorSaquesHoje() { return contadorSaquesHoje; }
    public LocalDate getDataContador() { return dataContador; }
}
