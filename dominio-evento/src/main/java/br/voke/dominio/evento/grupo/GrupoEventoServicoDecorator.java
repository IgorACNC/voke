package br.voke.dominio.evento.grupo;

import java.util.Objects;
import java.util.UUID;

/**
 * Decorator base abstrato (Decorator) no padrão GoF. Delega todas as operações
 * ao componente decorado, permitindo que subclasses sobrescrevam apenas os
 * métodos relevantes.
 */
public abstract class GrupoEventoServicoDecorator implements GrupoEventoServicoInterface {

    protected final GrupoEventoServicoInterface decorado;

    protected GrupoEventoServicoDecorator(GrupoEventoServicoInterface decorado) {
        Objects.requireNonNull(decorado, "Componente decorado é obrigatório");
        this.decorado = decorado;
    }

    @Override
    public GrupoEvento criar(String nome, String regras, UUID eventoId, UUID organizadorId) {
        return decorado.criar(nome, regras, eventoId, organizadorId);
    }

    @Override
    public void adicionarMembro(GrupoEventoId grupoId, UUID participanteId,
                                 boolean possuiInscricao, int idadeParticipante) {
        decorado.adicionarMembro(grupoId, participanteId, possuiInscricao, idadeParticipante);
    }

    @Override
    public void editarRegras(GrupoEventoId grupoId, String novasRegras, UUID solicitanteId) {
        decorado.editarRegras(grupoId, novasRegras, solicitanteId);
    }

    @Override
    public void remover(GrupoEventoId grupoId, UUID solicitanteId) {
        decorado.remover(grupoId, solicitanteId);
    }
}
