package br.voke.dominio.evento.notificacao;

import java.util.Iterator;
import java.util.UUID;

/**
 * Interface Iterator do padrão GoF.
 *
 * Define o contrato para percorrer participantes elegíveis de forma
 * sequencial, sem expor a estrutura interna da coleção de inscrições.
 */
public interface IteradorParticipantesElegiveis extends Iterator<UUID> {

    /**
     * Verifica se existe um próximo participante elegível.
     *
     * @return true se houver mais participantes com inscrição ativa
     */
    @Override
    boolean hasNext();

    /**
     * Retorna o UUID do próximo participante elegível.
     *
     * @return UUID do participante
     * @throws java.util.NoSuchElementException se não houver mais elementos
     */
    @Override
    UUID next();
}
