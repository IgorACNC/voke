package br.voke.dominio.evento.grupo;

import br.voke.dominio.evento.excecao.AcessoGrupoNegadoException;

import java.util.Objects;
import java.util.UUID;

/**
 * Decorator concreto — RN4 (Privilégio de criação e moderação).
 * Intercepta operações de criação ({@code criar}), edição ({@code editarRegras})
 * e exclusão ({@code remover}) para validar se o solicitante é o organizador
 * dono do evento associado ao grupo.
 *
 * <p>Em {@code criar} a comparação é direta entre {@code solicitanteId} e
 * {@code organizadorId} recebidos no método (não há grupo persistido ainda).
 * Em {@code editarRegras}/{@code remover} a comparação é feita após carregar
 * o agregado do repositório.</p>
 */
public class PrivilegioOrganizadorGrupoDecorator extends GrupoEventoServicoDecorator {

    private final GrupoEventoRepositorio repositorio;

    public PrivilegioOrganizadorGrupoDecorator(GrupoEventoServicoInterface decorado,
                                                GrupoEventoRepositorio repositorio) {
        super(decorado);
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
    }

    @Override
    public GrupoEvento criar(String nome, String regras, UUID eventoId, UUID organizadorId,
                              UUID solicitanteId) {
        Objects.requireNonNull(solicitanteId, "Solicitante é obrigatório");
        Objects.requireNonNull(organizadorId, "Organizador é obrigatório");
        if (!organizadorId.equals(solicitanteId)) {
            throw new AcessoGrupoNegadoException(
                    "Apenas o organizador do evento pode criar o grupo oficial");
        }
        return super.criar(nome, regras, eventoId, organizadorId, solicitanteId);
    }

    @Override
    public void editarRegras(GrupoEventoId grupoId, String novasRegras, UUID solicitanteId) {
        validarOrganizador(grupoId, solicitanteId);
        super.editarRegras(grupoId, novasRegras, solicitanteId);
    }

    @Override
    public void remover(GrupoEventoId grupoId, UUID solicitanteId) {
        validarOrganizador(grupoId, solicitanteId);
        super.remover(grupoId, solicitanteId);
    }

    private void validarOrganizador(GrupoEventoId grupoId, UUID solicitanteId) {
        GrupoEvento grupo = repositorio.buscarPorId(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));
        if (!grupo.getOrganizadorId().equals(solicitanteId)) {
            throw new AcessoGrupoNegadoException("Apenas o organizador pode realizar esta operação");
        }
    }
}
