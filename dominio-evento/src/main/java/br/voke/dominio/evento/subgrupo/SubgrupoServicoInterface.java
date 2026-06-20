package br.voke.dominio.evento.subgrupo;

import java.util.UUID;

public interface SubgrupoServicoInterface {

    Subgrupo criar(String nome, String descricao, String regras, UUID grupoEventoId,
                   CategoriaSubgrupo categoria, TipoSubgrupo tipo, int limiteMembros,
                   UUID solicitanteId, boolean solicitanteEhOrganizador);

    void adicionarMembro(SubgrupoId subgrupoId, UUID participanteId, UUID solicitanteId,
                         boolean ehMembroDoGrupoPrincipal, boolean solicitanteEhGestor);

    void removerMembro(SubgrupoId subgrupoId, UUID participanteId, UUID solicitanteId,
                       boolean solicitanteEhGestor);

    void editarRegras(SubgrupoId subgrupoId, String novasRegras, String novaDescricao,
                      UUID solicitanteId, boolean solicitanteEhGestor);

    void promoverModerador(SubgrupoId subgrupoId, UUID participanteId, UUID solicitanteId,
                           boolean solicitanteEhOrganizador);

    void removerModerador(SubgrupoId subgrupoId, UUID solicitanteId,
                          boolean solicitanteEhOrganizador);

    void remover(SubgrupoId subgrupoId, UUID solicitanteId, boolean solicitanteEhOrganizador);
}
