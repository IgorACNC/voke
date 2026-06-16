package br.voke.dominio.evento.subgrupo;

import java.util.Objects;
import java.util.UUID;

/**
 * Decorator base abstrato (Decorator) no padrão GoF. Delega todas as operações
 * ao componente decorado.
 */
public abstract class SubgrupoServicoDecorator implements SubgrupoServicoInterface {

    protected final SubgrupoServicoInterface decorado;

    protected SubgrupoServicoDecorator(SubgrupoServicoInterface decorado) {
        Objects.requireNonNull(decorado, "Componente decorado é obrigatório");
        this.decorado = decorado;
    }

    @Override
    public Subgrupo criar(String nome, String descricao, String regras, UUID grupoEventoId,
                          CategoriaSubgrupo categoria, TipoSubgrupo tipo, int limiteMembros,
                          UUID solicitanteId, boolean solicitanteEhOrganizador) {
        return decorado.criar(nome, descricao, regras, grupoEventoId, categoria, tipo,
                limiteMembros, solicitanteId, solicitanteEhOrganizador);
    }

    @Override
    public void adicionarMembro(SubgrupoId subgrupoId, UUID participanteId, UUID solicitanteId,
                                 boolean ehMembroDoGrupoPrincipal, boolean solicitanteEhGestor) {
        decorado.adicionarMembro(subgrupoId, participanteId, solicitanteId,
                ehMembroDoGrupoPrincipal, solicitanteEhGestor);
    }

    @Override
    public void removerMembro(SubgrupoId subgrupoId, UUID participanteId, UUID solicitanteId,
                              boolean solicitanteEhGestor) {
        decorado.removerMembro(subgrupoId, participanteId, solicitanteId, solicitanteEhGestor);
    }

    @Override
    public void editarRegras(SubgrupoId subgrupoId, String novasRegras, String novaDescricao,
                             UUID solicitanteId, boolean solicitanteEhGestor) {
        decorado.editarRegras(subgrupoId, novasRegras, novaDescricao, solicitanteId, solicitanteEhGestor);
    }

    @Override
    public void promoverModerador(SubgrupoId subgrupoId, UUID participanteId, UUID solicitanteId,
                                   boolean solicitanteEhOrganizador) {
        decorado.promoverModerador(subgrupoId, participanteId, solicitanteId, solicitanteEhOrganizador);
    }

    @Override
    public void removerModerador(SubgrupoId subgrupoId, UUID solicitanteId,
                                  boolean solicitanteEhOrganizador) {
        decorado.removerModerador(subgrupoId, solicitanteId, solicitanteEhOrganizador);
    }

    @Override
    public void remover(SubgrupoId subgrupoId, UUID solicitanteId, boolean solicitanteEhOrganizador) {
        decorado.remover(subgrupoId, solicitanteId, solicitanteEhOrganizador);
    }
}
