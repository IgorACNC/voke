package br.voke.aplicacao.evento;

import br.voke.dominio.evento.subgrupo.SubgrupoId;
import br.voke.dominio.evento.subgrupo.SubgrupoServicoInterface;
import br.voke.dominio.evento.subgrupo.solicitacao.SolicitacaoSubgrupo;
import br.voke.dominio.evento.subgrupo.solicitacao.SolicitacaoSubgrupoId;
import br.voke.dominio.evento.subgrupo.solicitacao.SolicitacaoSubgrupoServico;

import java.util.Objects;
import java.util.UUID;

/**
 * Aprova uma solicitação E adiciona o participante como membro do subgrupo.
 * Operação composta: marca a solicitação como APROVADA e materializa o membro.
 */
public class AprovarSolicitacaoSubgrupoCasoDeUso {

    private final SolicitacaoSubgrupoServico solicitacaoServico;
    private final SubgrupoServicoInterface subgrupoServico;

    public AprovarSolicitacaoSubgrupoCasoDeUso(SolicitacaoSubgrupoServico solicitacaoServico,
                                                SubgrupoServicoInterface subgrupoServico) {
        Objects.requireNonNull(solicitacaoServico);
        Objects.requireNonNull(subgrupoServico);
        this.solicitacaoServico = solicitacaoServico;
        this.subgrupoServico = subgrupoServico;
    }

    public SolicitacaoSubgrupo executar(UUID solicitacaoId, UUID decididoPor,
                                         boolean decididoPorEhGestor) {
        SolicitacaoSubgrupo aprovada = solicitacaoServico.aprovar(
                new SolicitacaoSubgrupoId(solicitacaoId), decididoPor, decididoPorEhGestor);
        // Adiciona o membro via SubgrupoServico (com flag gestor=true para passar pelo decorator FECHADO).
        // ehMembroDoGrupoPrincipal=true porque já foi validado no momento da solicitação.
        subgrupoServico.adicionarMembro(new SubgrupoId(aprovada.getSubgrupoId()),
                aprovada.getParticipanteId(), decididoPor, true, true);
        return aprovada;
    }
}
