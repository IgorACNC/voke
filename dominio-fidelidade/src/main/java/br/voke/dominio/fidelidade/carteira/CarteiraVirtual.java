package br.voke.dominio.fidelidade.carteira;

import br.voke.dominio.compartilhado.EntidadeBase;
import br.voke.dominio.fidelidade.excecao.LimiteFrequenciaSaqueException;
import br.voke.dominio.fidelidade.excecao.LimiteRemocaoException;
import br.voke.dominio.fidelidade.excecao.SaldoInsuficienteException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class CarteiraVirtual extends EntidadeBase<CarteiraVirtualId> {

    private static final BigDecimal LIMITE_REMOCAO = new BigDecimal("500.00");
    private static final int LIMITE_SAQUES_DIA = 1;

    private final UUID participanteId;
    private BigDecimal saldo;
    private BigDecimal totalInseridoHoje;
    private int contadorSaquesHoje;

    public CarteiraVirtual(CarteiraVirtualId id, UUID participanteId) {
        super(id);
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        this.participanteId = participanteId;
        this.saldo = BigDecimal.ZERO;
        this.totalInseridoHoje = BigDecimal.ZERO;
        this.contadorSaquesHoje = 0;
    }

    public void adicionarSaldo(BigDecimal valor) {
        adicionarSaldo(valor, new InsercaoSaldoPadrao());
    }

    public void adicionarSaldo(BigDecimal valor, EstrategiaInsercaoSaldo estrategia) {
        Objects.requireNonNull(valor, "Valor é obrigatório");
        Objects.requireNonNull(estrategia, "Estratégia de inserção é obrigatória");
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo");
        }
        estrategia.validar(totalInseridoHoje, valor);
        this.saldo = this.saldo.add(valor);
        this.totalInseridoHoje = this.totalInseridoHoje.add(valor);
    }

    public void removerSaldo(BigDecimal valor) {
        Objects.requireNonNull(valor, "Valor é obrigatório");
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo");
        }
        if (contadorSaquesHoje >= LIMITE_SAQUES_DIA) {
            throw new LimiteFrequenciaSaqueException();
        }
        if (valor.compareTo(LIMITE_REMOCAO) > 0) {
            throw new LimiteRemocaoException();
        }
        if (valor.compareTo(saldo) > 0) {
            throw new SaldoInsuficienteException();
        }
        this.saldo = this.saldo.subtract(valor);
        this.contadorSaquesHoje++;
    }

    public void debitar(BigDecimal valor) {
        Objects.requireNonNull(valor, "Valor é obrigatório");
        if (valor.compareTo(saldo) > 0) {
            throw new SaldoInsuficienteException();
        }
        this.saldo = this.saldo.subtract(valor);
    }

    public void creditar(BigDecimal valor) {
        Objects.requireNonNull(valor, "Valor é obrigatório");
        this.saldo = this.saldo.add(valor);
    }

    public void resetarLimiteDiario() {
        this.totalInseridoHoje = BigDecimal.ZERO;
        this.contadorSaquesHoje = 0;
    }

    public UUID getParticipanteId() { return participanteId; }
    public BigDecimal getSaldo() { return saldo; }
    public BigDecimal getTotalInseridoHoje() { return totalInseridoHoje; }
    public int getContadorSaquesHoje() { return contadorSaquesHoje; }
}