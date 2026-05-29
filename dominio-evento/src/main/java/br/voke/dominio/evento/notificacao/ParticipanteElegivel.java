package br.voke.dominio.evento.notificacao;

import java.util.Iterator;
import java.util.UUID;

/**
 * Interface Aggregate do padrão Iterator (GoF).
 *
 * Representa uma coleção de participantes elegíveis para receber notificações,
 * encapsulando a lógica de filtragem da RN 1 (Público-Alvo Restrito).
 *
 * O mecanismo de disparo de notificações consome esta interface sem conhecer
 * detalhes de consultas ao banco ou estruturas de listas internas.
 */
public interface ParticipanteElegivel extends Iterable<UUID> {

    /**
     * Cria um novo iterador que percorre apenas os participantes
     * com inscrição ativa no evento.
     *
     * @return iterador de UUIDs dos participantes elegíveis
     */
    @Override
    Iterator<UUID> iterator();
}
