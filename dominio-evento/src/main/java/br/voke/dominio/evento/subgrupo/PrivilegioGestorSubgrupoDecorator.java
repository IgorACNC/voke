package br.voke.dominio.evento.subgrupo;

import java.util.UUID;

/**
 * Decorator que valida privilégios de gestão. Para criar/excluir subgrupo,
 * promover/remover moderador, exige que o solicitante seja o organizador
 * do evento. Para editar regras / adicionar / remover membro, aceita
 * organizador OU moderador (flag solicitanteEhGestor).
 */
public class PrivilegioGestorSubgrupoDecorator extends SubgrupoServicoDecorator {

    public PrivilegioGestorSubgrupoDecorator(SubgrupoServicoInterface decorado) {
        super(decorado);
    }

    @Override
    public Subgrupo criar(String nome, String descricao, String regras, UUID grupoEventoId,
                          CategoriaSubgrupo categoria, TipoSubgrupo tipo, int limiteMembros,
                          UUID solicitanteId, boolean solicitanteEhOrganizador) {
        if (!solicitanteEhOrganizador) {
            throw new AcessoSubgrupoNegadoException(
                    "Apenas o organizador do evento pode criar subgrupos");
        }
        return super.criar(nome, descricao, regras, grupoEventoId, categoria, tipo,
                limiteMembros, solicitanteId, solicitanteEhOrganizador);
    }

    @Override
    public void adicionarMembro(SubgrupoId subgrupoId, UUID participanteId, UUID solicitanteId,
                                 boolean ehMembroDoGrupoPrincipal, boolean solicitanteEhGestor) {
        // Auto-inscrição (solicitanteId == participanteId) é permitida sem ser gestor;
        // adicionar terceiros exige gestor.
        if (!solicitanteId.equals(participanteId) && !solicitanteEhGestor) {
            throw new AcessoSubgrupoNegadoException();
        }
        super.adicionarMembro(subgrupoId, participanteId, solicitanteId,
                ehMembroDoGrupoPrincipal, solicitanteEhGestor);
    }

    @Override
    public void removerMembro(SubgrupoId subgrupoId, UUID participanteId, UUID solicitanteId,
                              boolean solicitanteEhGestor) {
        // Sair do próprio subgrupo é permitido; remover terceiro exige gestor.
        if (!solicitanteId.equals(participanteId) && !solicitanteEhGestor) {
            throw new AcessoSubgrupoNegadoException();
        }
        super.removerMembro(subgrupoId, participanteId, solicitanteId, solicitanteEhGestor);
    }

    @Override
    public void editarRegras(SubgrupoId subgrupoId, String novasRegras, String novaDescricao,
                             UUID solicitanteId, boolean solicitanteEhGestor) {
        if (!solicitanteEhGestor) {
            throw new AcessoSubgrupoNegadoException();
        }
        super.editarRegras(subgrupoId, novasRegras, novaDescricao, solicitanteId, solicitanteEhGestor);
    }

    @Override
    public void promoverModerador(SubgrupoId subgrupoId, UUID participanteId, UUID solicitanteId,
                                   boolean solicitanteEhOrganizador) {
        if (!solicitanteEhOrganizador) {
            throw new AcessoSubgrupoNegadoException(
                    "Apenas o organizador pode promover moderador");
        }
        super.promoverModerador(subgrupoId, participanteId, solicitanteId, solicitanteEhOrganizador);
    }

    @Override
    public void removerModerador(SubgrupoId subgrupoId, UUID solicitanteId,
                                  boolean solicitanteEhOrganizador) {
        if (!solicitanteEhOrganizador) {
            throw new AcessoSubgrupoNegadoException(
                    "Apenas o organizador pode remover moderador");
        }
        super.removerModerador(subgrupoId, solicitanteId, solicitanteEhOrganizador);
    }

    @Override
    public void remover(SubgrupoId subgrupoId, UUID solicitanteId, boolean solicitanteEhOrganizador) {
        if (!solicitanteEhOrganizador) {
            throw new AcessoSubgrupoNegadoException(
                    "Apenas o organizador pode excluir subgrupos");
        }
        super.remover(subgrupoId, solicitanteId, solicitanteEhOrganizador);
    }
}
