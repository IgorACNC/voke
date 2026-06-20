package br.voke.dominio.evento.subgrupo;

import java.util.UUID;

/**
 * Decorator que bloqueia auto-inscrição em subgrupos FECHADOS — o caminho
 * correto nesse caso é uma SolicitacaoSubgrupo aprovada pelo gestor.
 * Gestores podem adicionar membros diretamente (passando flag true).
 */
public class TipoFechadoSubgrupoDecorator extends SubgrupoServicoDecorator {

    private final SubgrupoRepositorio repositorio;

    public TipoFechadoSubgrupoDecorator(SubgrupoServicoInterface decorado,
                                         SubgrupoRepositorio repositorio) {
        super(decorado);
        this.repositorio = repositorio;
    }

    @Override
    public void adicionarMembro(SubgrupoId subgrupoId, UUID participanteId, UUID solicitanteId,
                                 boolean ehMembroDoGrupoPrincipal, boolean solicitanteEhGestor) {
        Subgrupo subgrupo = repositorio.buscarPorId(subgrupoId)
                .orElseThrow(() -> new IllegalArgumentException("Subgrupo não encontrado"));
        if (subgrupo.getTipo() == TipoSubgrupo.FECHADO && !solicitanteEhGestor) {
            throw new SubgrupoFechadoException();
        }
        super.adicionarMembro(subgrupoId, participanteId, solicitanteId,
                ehMembroDoGrupoPrincipal, solicitanteEhGestor);
    }
}
