package br.voke.aplicacao.evento;

import br.voke.dominio.evento.evento.CancelamentoInscricoesEvento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoServico;
import br.voke.dominio.evento.grupo.GrupoEventoServico;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Cancelamento de evento pelo organizador (RN4).
 *
 * Fluxo:
 *   1. Cancela o agregado Evento (status -> CANCELADO, lote vigente é encerrado).
 *   2. Dispara a porta {@link CancelamentoInscricoesEvento}, que delega ao
 *      contexto de Inscrição o cancelamento + estorno de 100% na carteira.
 *   3. Congela o snapshot da estatística do evento (RN05 do dashboard).
 *
 * O caso de uso não conhece os detalhes do contexto de Inscrição nem de
 * Fidelidade — fala apenas com a porta de saída do domínio de Evento.
 */
public class CancelarEventoCasoDeUso {

    private final EventoServico servico;
    private final CancelamentoInscricoesEvento cancelamentoInscricoes;
    private final AtualizadorEstatisticaListener atualizadorEstatistica;
    private final GrupoEventoServico grupoEventoServico;

    public CancelarEventoCasoDeUso(EventoServico servico,
                                   CancelamentoInscricoesEvento cancelamentoInscricoes,
                                   AtualizadorEstatisticaListener atualizadorEstatistica,
                                   GrupoEventoServico grupoEventoServico) {
        Objects.requireNonNull(servico);
        Objects.requireNonNull(cancelamentoInscricoes);
        Objects.requireNonNull(atualizadorEstatistica);
        Objects.requireNonNull(grupoEventoServico);
        this.servico = servico;
        this.cancelamentoInscricoes = cancelamentoInscricoes;
        this.atualizadorEstatistica = atualizadorEstatistica;
        this.grupoEventoServico = grupoEventoServico;
    }

    public Resultado executar(UUID eventoId) {
        servico.cancelar(new EventoId(eventoId));
        CancelamentoInscricoesEvento.Resultado estorno =
                cancelamentoInscricoes.cancelarInscricoesDoEvento(eventoId);
        atualizadorEstatistica.aoEncerrarEvento(eventoId);
        // RN3 (grupos): remove o grupo de comunicação vinculado ao evento
        grupoEventoServico.removerPorEvento(eventoId);
        return new Resultado(estorno.inscricoesReembolsadas(), estorno.valorTotalReembolsado());
    }

    public record Resultado(int inscricoesReembolsadas, BigDecimal valorTotalReembolsado) {}
}
