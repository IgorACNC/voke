package br.voke.dominio.fidelidade.comissao;

import br.voke.dominio.compartilhado.EntidadeBase;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class ComissaoParceiro extends EntidadeBase<ComissaoParceiroId> {

    private final UUID parceiroId;
    private final UUID cupomId;
    private final UUID inscricaoId;
    private final BigDecimal valor;
    private StatusComissao status;
    private final LocalDateTime dataHora;

    public ComissaoParceiro(ComissaoParceiroId id, UUID parceiroId, UUID cupomId, UUID inscricaoId, BigDecimal valor) {
        super(id);
        Objects.requireNonNull(parceiroId, "ID do parceiro é obrigatório");
        Objects.requireNonNull(cupomId, "ID do cupom é obrigatório");
        Objects.requireNonNull(inscricaoId, "ID da inscrição é obrigatório");
        Objects.requireNonNull(valor, "Valor da comissão é obrigatório");
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da comissão deve ser maior que zero");
        }
        this.parceiroId = parceiroId;
        this.cupomId = cupomId;
        this.inscricaoId = inscricaoId;
        this.valor = valor;
        this.status = StatusComissao.CREDITADA;
        this.dataHora = LocalDateTime.now();
    }

    public void estornar() {
        if (this.status == StatusComissao.ESTORNADA) {
            throw new IllegalStateException("Comissão já está estornada");
        }
        this.status = StatusComissao.ESTORNADA;
    }

    public UUID getParceiroId() { return parceiroId; }
    public UUID getCupomId() { return cupomId; }
    public UUID getInscricaoId() { return inscricaoId; }
    public BigDecimal getValor() { return valor; }
    public StatusComissao getStatus() { return status; }
    public LocalDateTime getDataHora() { return dataHora; }

    // Construtor para reconstrução via repositório
    public ComissaoParceiro(ComissaoParceiroId id, UUID parceiroId, UUID cupomId, UUID inscricaoId, BigDecimal valor, StatusComissao status, LocalDateTime dataHora) {
        super(id);
        this.parceiroId = parceiroId;
        this.cupomId = cupomId;
        this.inscricaoId = inscricaoId;
        this.valor = valor;
        this.status = status;
        this.dataHora = dataHora;
    }
}
