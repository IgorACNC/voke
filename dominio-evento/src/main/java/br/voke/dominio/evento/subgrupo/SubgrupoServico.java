package br.voke.dominio.evento.subgrupo;

import java.util.Objects;
import java.util.UUID;

/**
 * Componente Concreto (ConcreteComponent) no padrão Decorator.
 * Responsável pela manipulação direta da entidade e persistência.
 * As regras de validação (gestor, tipo fechado, membership do grupo principal)
 * são tratadas pelos decorators da cadeia.
 */
public class SubgrupoServico implements SubgrupoServicoInterface {

    private final SubgrupoRepositorio repositorio;

    public SubgrupoServico(SubgrupoRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
    }

    @Override
    public Subgrupo criar(String nome, String descricao, String regras, UUID grupoEventoId,
                          CategoriaSubgrupo categoria, TipoSubgrupo tipo, int limiteMembros,
                          UUID solicitanteId, boolean solicitanteEhOrganizador) {
        Subgrupo subgrupo = new Subgrupo(SubgrupoId.novo(), nome, descricao, regras,
                grupoEventoId, categoria, tipo, limiteMembros);
        repositorio.salvar(subgrupo);
        return subgrupo;
    }

    @Override
    public void adicionarMembro(SubgrupoId subgrupoId, UUID participanteId, UUID solicitanteId,
                                 boolean ehMembroDoGrupoPrincipal, boolean solicitanteEhGestor) {
        Subgrupo subgrupo = buscar(subgrupoId);
        subgrupo.adicionarMembro(participanteId);
        repositorio.salvar(subgrupo);
    }

    @Override
    public void removerMembro(SubgrupoId subgrupoId, UUID participanteId, UUID solicitanteId,
                              boolean solicitanteEhGestor) {
        Subgrupo subgrupo = buscar(subgrupoId);
        subgrupo.removerMembro(participanteId);
        repositorio.salvar(subgrupo);
    }

    @Override
    public void editarRegras(SubgrupoId subgrupoId, String novasRegras, String novaDescricao,
                             UUID solicitanteId, boolean solicitanteEhGestor) {
        Subgrupo subgrupo = buscar(subgrupoId);
        subgrupo.atualizarRegras(novasRegras);
        subgrupo.atualizarDescricao(novaDescricao);
        repositorio.salvar(subgrupo);
    }

    @Override
    public void promoverModerador(SubgrupoId subgrupoId, UUID participanteId, UUID solicitanteId,
                                   boolean solicitanteEhOrganizador) {
        Subgrupo subgrupo = buscar(subgrupoId);
        subgrupo.promoverModerador(participanteId);
        repositorio.salvar(subgrupo);
    }

    @Override
    public void removerModerador(SubgrupoId subgrupoId, UUID solicitanteId,
                                  boolean solicitanteEhOrganizador) {
        Subgrupo subgrupo = buscar(subgrupoId);
        subgrupo.removerModerador();
        repositorio.salvar(subgrupo);
    }

    @Override
    public void remover(SubgrupoId subgrupoId, UUID solicitanteId, boolean solicitanteEhOrganizador) {
        buscar(subgrupoId);
        repositorio.remover(subgrupoId);
    }

    private Subgrupo buscar(SubgrupoId id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Subgrupo não encontrado"));
    }
}
