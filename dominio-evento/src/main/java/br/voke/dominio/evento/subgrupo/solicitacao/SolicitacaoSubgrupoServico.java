package br.voke.dominio.evento.subgrupo.solicitacao;

import br.voke.dominio.evento.subgrupo.AcessoSubgrupoNegadoException;
import br.voke.dominio.evento.subgrupo.MembroNaoEstaNoGrupoPrincipalException;
import br.voke.dominio.evento.subgrupo.SolicitacaoDuplicadaException;
import br.voke.dominio.evento.subgrupo.TipoSubgrupo;

import java.util.Objects;
import java.util.UUID;

public class SolicitacaoSubgrupoServico {

    private final SolicitacaoSubgrupoRepositorio repositorio;

    public SolicitacaoSubgrupoServico(SolicitacaoSubgrupoRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
    }

    public SolicitacaoSubgrupo solicitar(UUID subgrupoId, UUID participanteId, String mensagem,
                                          boolean ehMembroDoGrupoPrincipal, TipoSubgrupo tipo) {
        if (!ehMembroDoGrupoPrincipal) {
            throw new MembroNaoEstaNoGrupoPrincipalException();
        }
        if (tipo != TipoSubgrupo.FECHADO) {
            throw new IllegalArgumentException(
                    "Solicitação só faz sentido para subgrupos FECHADOS — em subgrupos ABERTOS entre direto");
        }
        repositorio.buscarPendentePorParticipanteESubgrupo(participanteId, subgrupoId)
                .ifPresent(s -> { throw new SolicitacaoDuplicadaException(); });

        SolicitacaoSubgrupo solicitacao = new SolicitacaoSubgrupo(
                SolicitacaoSubgrupoId.novo(), subgrupoId, participanteId, mensagem);
        repositorio.salvar(solicitacao);
        return solicitacao;
    }

    public SolicitacaoSubgrupo aprovar(SolicitacaoSubgrupoId id, UUID decididoPor,
                                        boolean decididoPorEhGestor) {
        if (!decididoPorEhGestor) {
            throw new AcessoSubgrupoNegadoException();
        }
        SolicitacaoSubgrupo solicitacao = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));
        solicitacao.aprovar(decididoPor);
        repositorio.salvar(solicitacao);
        return solicitacao;
    }

    public SolicitacaoSubgrupo rejeitar(SolicitacaoSubgrupoId id, UUID decididoPor,
                                        boolean decididoPorEhGestor) {
        if (!decididoPorEhGestor) {
            throw new AcessoSubgrupoNegadoException();
        }
        SolicitacaoSubgrupo solicitacao = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));
        solicitacao.rejeitar(decididoPor);
        repositorio.salvar(solicitacao);
        return solicitacao;
    }
}
