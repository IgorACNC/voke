package br.voke.infraestrutura.evento.cupom;

import br.voke.dominio.evento.cupom.Cupom;
import br.voke.dominio.evento.cupom.CupomId;
import br.voke.dominio.evento.cupom.TipoDesconto;
import br.voke.infraestrutura.compartilhado.DominioReflection;

import java.util.HashSet;

public final class CupomJpaMapper {

    private CupomJpaMapper() {
    }

    public static CupomJpa paraJpa(Cupom cupom) {
        return new CupomJpa(cupom.getId().getValor(), cupom.getCodigo(), cupom.getDesconto(),
                cupom.getTipoDesconto(), cupom.getOrganizadorId(), cupom.getEventoId(),
                cupom.getQuantidadeMaxima(), cupom.isAtivo(), cupom.getCpfsUtilizados());
    }

    public static Cupom paraDominio(CupomJpa jpa) {
        TipoDesconto tipo = jpa.getTipoDesconto() != null ? jpa.getTipoDesconto() : TipoDesconto.FIXO;
        Cupom cupom = new Cupom(new CupomId(jpa.getId()), jpa.getCodigo(), jpa.getDesconto(),
                tipo, jpa.getOrganizadorId(), jpa.getEventoId(), jpa.getQuantidadeMaxima());
        DominioReflection.definirCampo(cupom, "cpfsUtilizados", new HashSet<>(jpa.getCpfsUtilizados()));
        DominioReflection.definirCampo(cupom, "ativo", jpa.isAtivo());
        return cupom;
    }
}
