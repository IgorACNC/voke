package br.voke.dominio.fidelidade.carteira;

import br.voke.dominio.compartilhado.EntidadeBase;
import br.voke.dominio.fidelidade.excecao.LimiteFrequenciaSaqueException;
import br.voke.dominio.fidelidade.excecao.LimiteRemocaoException;
import br.voke.dominio.fidelidade.excecao.SaldoInsuficienteException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class CarteiraVirtual extends EntidadeBase<CarteiraVirtualId> {

    private static final BigDecimal LIMITE_REMOCAO = new BigDecimal("500.00");
    private static final int LIMITE_SAQUES_DIA = 1;

    private final UUID participanteId;
    /** RN3 - dinheiro real depositado pelo usuário (PIX/Cartão). Pode ser sacado. */
    private BigDecimal saldo;
    /** RN3 - bônus/recompensa/comissão. Pode comprar ingresso, NÃO pode ser sacado. */
    private BigDecimal saldoPromocional;
    private BigDecimal totalInseridoHoje;
    private int contadorSaquesHoje;
    private LocalDate dataContador;

    public CarteiraVirtual(CarteiraVirtualId id, UUID participanteId) {
        super(id);
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        this.participanteId = participanteId;
        this.saldo = BigDecimal.ZERO;
        this.saldoPromocional = BigDecimal.ZERO;
        this.totalInseridoHoje = BigDecimal.ZERO;
        this.contadorSaquesHoje = 0;
        this.dataContador = LocalDate.now();
    }

    private void resetarSeNovoDia() {
        LocalDate hoje = LocalDate.now();
        if (dataContador == null || !dataContador.equals(hoje)) {
            this.totalInseridoHoje = BigDecimal.ZERO;
            this.contadorSaquesHoje = 0;
            this.dataContador = hoje;
        }
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
        resetarSeNovoDia();
        estrategia.validar(totalInseridoHoje, valor);
        this.saldo = this.saldo.add(valor);
        this.totalInseridoHoje = this.totalInseridoHoje.add(valor);
    }

    public void removerSaldo(BigDecimal valor) {
        Objects.requireNonNull(valor, "Valor é obrigatório");
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo");
        }
        resetarSeNovoDia();
        if (contadorSaquesHoje >= LIMITE_SAQUES_DIA) {
            throw new LimiteFrequenciaSaqueException();
        }
        if (valor.compareTo(LIMITE_REMOCAO) > 0) {
            throw new LimiteRemocaoException();
        }
        // RN3 - saque opera apenas sobre o saldo real.
        if (valor.compareTo(saldo) > 0) {
            throw new SaldoInsuficienteException();
        }
        this.saldo = this.saldo.subtract(valor);
        this.contadorSaquesHoje++;
        this.dataContador = LocalDate.now();
    }

    public void debitar(BigDecimal valor) {
        Objects.requireNonNull(valor, "Valor é obrigatório");
        // RN3 - compra consome primeiro o saldo promocional, depois o real.
        BigDecimal totalDisponivel = saldo.add(saldoPromocional);
        if (valor.compareTo(totalDisponivel) > 0) {
            throw new SaldoInsuficienteException();
        }
        BigDecimal restante = valor;
        if (saldoPromocional.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal usaPromocional = saldoPromocional.min(restante);
            this.saldoPromocional = this.saldoPromocional.subtract(usaPromocional);
            restante = restante.subtract(usaPromocional);
        }
        if (restante.compareTo(BigDecimal.ZERO) > 0) {
            this.saldo = this.saldo.subtract(restante);
        }
    }

    public void creditar(BigDecimal valor) {
        Objects.requireNonNull(valor, "Valor é obrigatório");
        // RN3 - bônus/recompensa entra como saldo promocional (não sacável).
        this.saldoPromocional = this.saldoPromocional.add(valor);
    }

    /** Estorno (cancelamento de inscrição) devolve para o saldo real porque
     *  o dinheiro originalmente debitado veio do bolso real do usuário. */
    public void creditarReembolso(BigDecimal valor) {
        Objects.requireNonNull(valor, "Valor é obrigatório");
        this.saldo = this.saldo.add(valor);
    }

    public void resetarLimiteDiario() {
        this.totalInseridoHoje = BigDecimal.ZERO;
        this.contadorSaquesHoje = 0;
        this.dataContador = LocalDate.now();
    }

    public UUID getParticipanteId() { return participanteId; }
    /** Saldo real (sacável). */
    public BigDecimal getSaldo() { return saldo; }
    /** Saldo promocional (não sacável). */
    public BigDecimal getSaldoPromocional() {
        return saldoPromocional != null ? saldoPromocional : BigDecimal.ZERO;
    }
    /** Saldo total exibido ao usuário (real + promocional). */
    public BigDecimal getSaldoTotal() {
        return getSaldo().add(getSaldoPromocional());
    }
    public BigDecimal getTotalInseridoHoje() { return totalInseridoHoje; }
    public int getContadorSaquesHoje() { return contadorSaquesHoje; }
    public LocalDate getDataContador() { return dataContador; }
}