package br.voke.aplicacao.evento;

import br.voke.dominio.evento.subgrupo.Subgrupo;
import br.voke.dominio.evento.subgrupo.SubgrupoId;
import br.voke.dominio.evento.subgrupo.SubgrupoRepositorio;
import br.voke.dominio.evento.subgrupo.solicitacao.SolicitacaoSubgrupo;
import br.voke.dominio.evento.subgrupo.solicitacao.SolicitacaoSubgrupoServico;

import java.util.Objects;
import java.util.UUID;

public class SolicitarEntradaSubgrupoCasoDeUso {

    private final SolicitacaoSubgrupoServico servico;
    private final SubgrupoRepositorio subgrupoRepositorio;

    public SolicitarEntradaSubgrupoCasoDeUso(SolicitacaoSubgrupoServico servico,
                                              SubgrupoRepositorio subgrupoRepositorio) {
        Objects.requireNonNull(servico);
        Objects.requireNonNull(subgrupoRepositorio);
        this.servico = servico;
        this.subgrupoRepositorio = subgrupoRepositorio;
    }

    public SolicitacaoSubgrupo executar(UUID subgrupoId, UUID participanteId, String mensagem,
                                         boolean ehMembroDoGrupoPrincipal) {
        Subgrupo subgrupo = subgrupoRepositorio.buscarPorId(new SubgrupoId(subgrupoId))
                .orElseThrow(() -> new IllegalArgumentException("Subgrupo não encontrado"));
        return servico.solicitar(subgrupoId, participanteId, mensagem,
                ehMembroDoGrupoPrincipal, subgrupo.getTipo());
    }
}
