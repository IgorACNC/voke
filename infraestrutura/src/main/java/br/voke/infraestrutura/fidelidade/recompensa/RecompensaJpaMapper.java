package br.voke.infraestrutura.fidelidade.recompensa;

import br.voke.dominio.fidelidade.recompensa.Recompensa;
import br.voke.dominio.fidelidade.recompensa.RecompensaId;
import br.voke.infraestrutura.compartilhado.DominioReflection;

public final class RecompensaJpaMapper {

    private RecompensaJpaMapper() {
    }

    public static RecompensaJpa paraJpa(Recompensa recompensa) {
        return new RecompensaJpa(recompensa.getId().getValor(), recompensa.getNome(),
                recompensa.getDescricao(), recompensa.getCustoEmPontos(), recompensa.getEstoqueTotal(),
                recompensa.getEstoqueResgatado(), recompensa.getOrganizadorId(),
                recompensa.getUltimaAlteracaoValor(), recompensa.isAtiva());
    }

    public static Recompensa paraDominio(RecompensaJpa jpa) {
        Recompensa recompensa = new Recompensa(new RecompensaId(jpa.getId()), jpa.getNome(), jpa.getDescricao(),
                jpa.getCustoEmPontos(), jpa.getEstoqueTotal(), jpa.getOrganizadorId());
        DominioReflection.definirCampo(recompensa, "estoqueResgatado", jpa.getEstoqueResgatado());
        DominioReflection.definirCampo(recompensa, "ultimaAlteracaoValor", jpa.getUltimaAlteracaoValor());
        DominioReflection.definirCampo(recompensa, "ativa", jpa.isAtiva());
        return recompensa;
    }
}
