package br.voke.dominio.evento.notificacao;

import java.util.Set;
import java.util.UUID;

/**
 * Interface Strategy (GoF) para critérios de segmentação de destinatários.
 *
 * <p>Cada implementação define uma regra de filtragem diferente,
 * permitindo ao organizador escolher quem recebe a notificação
 * sem alterar o mecanismo de envio.</p>
 */
public interface CriterioSegmentacao {

    /**
     * Filtra os participantes elegíveis de acordo com o critério.
     *
     * @param todosElegiveis conjunto de todos os participantes com inscrição ativa
     * @return subconjunto dos elegíveis que atendem ao critério
     */
    Set<UUID> filtrar(Set<UUID> todosElegiveis);

    /**
     * Retorna uma descrição textual do critério para auditoria.
     *
     * @return descrição do critério aplicado (ex: "GRUPO:VIP", "LOTE:3")
     */
    String descricao();
}
