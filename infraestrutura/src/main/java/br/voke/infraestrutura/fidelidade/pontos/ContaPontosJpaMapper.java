package br.voke.infraestrutura.fidelidade.pontos;

import br.voke.dominio.fidelidade.pontos.ContaPontos;
import br.voke.dominio.fidelidade.pontos.ContaPontosId;
import br.voke.infraestrutura.compartilhado.DominioReflection;

public final class ContaPontosJpaMapper {

    private ContaPontosJpaMapper() {
    }

    public static ContaPontosJpa paraJpa(ContaPontos conta) {
        return new ContaPontosJpa(conta.getId().getValor(), conta.getParticipanteId(), conta.getSaldo());
    }

    public static ContaPontos paraDominio(ContaPontosJpa jpa) {
        ContaPontos conta = new ContaPontos(new ContaPontosId(jpa.getId()), jpa.getParticipanteId());
        DominioReflection.definirCampo(conta, "saldo", jpa.getSaldo());
        return conta;
    }
}