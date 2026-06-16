package br.voke.aplicacao.evento;

import br.voke.dominio.evento.subgrupo.solicitacao.SolicitacaoSubgrupo;
import br.voke.dominio.evento.subgrupo.solicitacao.SolicitacaoSubgrupoId;
import br.voke.dominio.evento.subgrupo.solicitacao.SolicitacaoSubgrupoServico;

import java.util.Objects;
import java.util.UUID;

public class RejeitarSolicitacaoSubgrupoCasoDeUso {

    private final SolicitacaoSubgrupoServico servico;

    public RejeitarSolicitacaoSubgrupoCasoDeUso(SolicitacaoSubgrupoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public SolicitacaoSubgrupo executar(UUID solicitacaoId, UUID decididoPor,
                                         boolean decididoPorEhGestor) {
        return servico.rejeitar(new SolicitacaoSubgrupoId(solicitacaoId), decididoPor,
                decididoPorEhGestor);
    }
}
