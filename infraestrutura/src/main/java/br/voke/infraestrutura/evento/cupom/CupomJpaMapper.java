package br.voke.infraestrutura.evento.cupom;

import br.voke.dominio.evento.cupom.Cupom;
import br.voke.dominio.evento.cupom.CupomId;
import br.voke.infraestrutura.compartilhado.DominioReflection;

import java.util.HashSet;

public final class CupomJpaMapper {

    private CupomJpaMapper() {
    }

    public static CupomJpa paraJpa(Cupom cupom) {
        return new CupomJpa(cupom.getId().getValor(), cupom.getCodigo(), cupom.getDesconto(),
                cupom.getOrganizadorId(), cupom.getEventoId(), cupom.getQuantidadeMaxima(),
                cupom.getCpfsUtilizados());
    }

    public static Cupom paraDominio(CupomJpa jpa) {
        Cupom cupom = new Cupom(new CupomId(jpa.getId()), jpa.getCodigo(), jpa.getDesconto(),
                jpa.getOrganizadorId(), jpa.getEventoId(), jpa.getQuantidadeMaxima());
        DominioReflection.definirCampo(cupom, "cpfsUtilizados", new HashSet<>(jpa.getCpfsUtilizados()));
        return cupom;
    }
}
