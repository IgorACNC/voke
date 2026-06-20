package br.voke.infraestrutura.fidelidade.comissao;

import br.voke.dominio.fidelidade.comissao.ComissaoParceiro;
import br.voke.dominio.fidelidade.comissao.ComissaoParceiroId;

public final class ComissaoParceiroJpaMapper {

    private ComissaoParceiroJpaMapper() {}

    public static ComissaoParceiroJpa paraJpa(ComissaoParceiro comissao) {
        return new ComissaoParceiroJpa(
                comissao.getId().getValor(),
                comissao.getParceiroId(),
                comissao.getCupomId(),
                comissao.getInscricaoId(),
                comissao.getValor(),
                comissao.getStatus(),
                comissao.getDataHora()
        );
    }

    public static ComissaoParceiro paraDominio(ComissaoParceiroJpa jpa) {
        return new ComissaoParceiro(
                ComissaoParceiroId.de(jpa.getId().toString()),
                jpa.getParceiroId(),
                jpa.getCupomId(),
                jpa.getInscricaoId(),
                jpa.getValor(),
                jpa.getStatus(),
                jpa.getDataHora()
        );
    }
}
