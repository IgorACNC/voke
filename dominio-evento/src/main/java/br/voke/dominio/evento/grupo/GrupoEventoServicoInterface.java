package br.voke.dominio.evento.grupo;

import java.util.UUID;

/**
 * Contrato do Componente (Component) no padrão Decorator para o serviço de
 * gerenciamento de grupos de evento. Tanto o serviço concreto quanto os
 * decorators implementam esta interface.
 */
public interface GrupoEventoServicoInterface {

    GrupoEvento criar(String nome, String regras, UUID eventoId, UUID organizadorId);

    void adicionarMembro(GrupoEventoId grupoId, UUID participanteId,
                         boolean possuiInscricao, int idadeParticipante);

    void editarRegras(GrupoEventoId grupoId, String novasRegras, UUID solicitanteId);

    void remover(GrupoEventoId grupoId, UUID solicitanteId);
}
