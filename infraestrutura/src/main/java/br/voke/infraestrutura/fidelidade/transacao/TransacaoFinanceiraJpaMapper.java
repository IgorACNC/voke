package br.voke.infraestrutura.fidelidade.transacao;

import br.voke.dominio.fidelidade.transacao.TransacaoFinanceira;
import br.voke.dominio.fidelidade.transacao.TransacaoFinanceiraId;
import br.voke.infraestrutura.compartilhado.DominioReflection;

public final class TransacaoFinanceiraJpaMapper {

    private TransacaoFinanceiraJpaMapper() {}

    public static TransacaoFinanceiraJpa paraJpa(TransacaoFinanceira t) {
        return new TransacaoFinanceiraJpa(
                t.getId().getValor(),
                t.getParticipanteId(),
                t.getTipo(),
                t.getValor(),
                t.getDescricao(),
                t.getDataHora());
    }

    public static TransacaoFinanceira paraDominio(TransacaoFinanceiraJpa jpa) {
        TransacaoFinanceira t = new TransacaoFinanceira(
                new TransacaoFinanceiraId(jpa.getId()),
                jpa.getParticipanteId(),
                jpa.getTipo(),
                jpa.getValor(),
                jpa.getDescricao());
        // sobrescreve dataHora com o valor persistido (o construtor seta LocalDateTime.now())
        DominioReflection.definirCampo(t, "dataHora", jpa.getDataHora());
        return t;
    }
}
