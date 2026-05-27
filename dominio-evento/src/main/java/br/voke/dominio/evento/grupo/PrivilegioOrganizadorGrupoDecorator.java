package br.voke.dominio.evento.grupo;

import br.voke.dominio.evento.excecao.AcessoGrupoNegadoException;

import java.util.Objects;
import java.util.UUID;

/**
 * Decorator concreto — RN4 (Privilégio de criação e moderação).
 * Intercepta operações de escrita ({@code editarRegras}) e exclusão
 * ({@code remover}) para validar se o solicitante é o organizador dono do
 * evento associado ao grupo.
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
