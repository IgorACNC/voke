package br.voke.infraestrutura.inscricao.inscricao;

import br.voke.dominio.inscricao.inscricao.Inscricao;
import br.voke.dominio.inscricao.inscricao.InscricaoId;
import br.voke.infraestrutura.compartilhado.DominioReflection;

public final class InscricaoJpaMapper {

    private InscricaoJpaMapper() {
    }

    public static InscricaoJpa paraJpa(Inscricao inscricao) {
        return new InscricaoJpa(inscricao.getId().getValor(), inscricao.getParticipanteId(),
                inscricao.getEventoId(), inscricao.getValorPago(), inscricao.getDataInscricao(),
                inscricao.getStatus());
    }

    public static Inscricao paraDominio(InscricaoJpa jpa) {
        Inscricao inscricao = new Inscricao(new InscricaoId(jpa.getId()), jpa.getParticipanteId(),
                jpa.getEventoId(), jpa.getValorPago());
        DominioReflection.definirCampo(inscricao, "dataInscricao", jpa.getDataInscricao());
        DominioReflection.definirCampo(inscricao, "status", jpa.getStatus());
        return inscricao;
    }
}
