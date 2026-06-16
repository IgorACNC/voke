package br.voke.infraestrutura.evento.subgrupo.solicitacao;

import br.voke.dominio.evento.subgrupo.solicitacao.SolicitacaoSubgrupo;
import br.voke.dominio.evento.subgrupo.solicitacao.SolicitacaoSubgrupoId;

public final class SolicitacaoSubgrupoJpaMapper {

    private SolicitacaoSubgrupoJpaMapper() {
    }

    public static SolicitacaoSubgrupoJpa paraJpa(SolicitacaoSubgrupo s) {
        return new SolicitacaoSubgrupoJpa(
                s.getId().getValor(),
                s.getSubgrupoId(),
                s.getParticipanteId(),
                s.getMensagem(),
                s.getStatus(),
                s.getDataSolicitacao(),
                s.getDataDecisao(),
                s.getDecididoPor());
    }

    public static SolicitacaoSubgrupo paraDominio(SolicitacaoSubgrupoJpa jpa) {
        return new SolicitacaoSubgrupo(
                new SolicitacaoSubgrupoId(jpa.getId()),
                jpa.getSubgrupoId(),
                jpa.getParticipanteId(),
                jpa.getMensagem(),
                jpa.getStatus(),
                jpa.getDataSolicitacao(),
                jpa.getDataDecisao(),
                jpa.getDecididoPor());
    }
}
