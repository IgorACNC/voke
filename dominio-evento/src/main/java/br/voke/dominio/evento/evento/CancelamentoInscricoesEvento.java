package br.voke.dominio.evento.evento;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Porta de saída do contexto de Evento para o contexto de Inscrição.
 *
 * RN4 - quando um evento é cancelado, todas as inscrições ativas devem ser
 * invalidadas e o valor pago integralmente reembolsado ao participante.
 * O contexto de Evento desconhece os detalhes (carteira, repositório de
 * inscrição); ele só dispara o efeito via esta porta.
 */
public interface CancelamentoInscricoesEvento {

    Resultado cancelarInscricoesDoEvento(UUID eventoId);

    record Resultado(int inscricoesReembolsadas, BigDecimal valorTotalReembolsado) {}
}
