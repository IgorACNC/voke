package br.voke.aplicacao.evento;

import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoServico;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualServico;
import br.voke.dominio.inscricao.inscricao.Inscricao;
import br.voke.dominio.inscricao.inscricao.InscricaoRepositorio;
import br.voke.dominio.inscricao.inscricao.StatusInscricao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Cancelamento de evento pelo organizador.
 * Regras:
 *  - Todas as inscricoes confirmadas (ou com check-in feito) sao canceladas.
 *  - O participante recebe 100% do valor pago de volta na carteira virtual,
 *    independentemente da proximidade da data (politica diferente do
 *    cancelamento por iniciativa do participante).
 *  - Snapshot da estatistica e congelado apos o cancelamento (RN05).
 */
public class CancelarEventoCasoDeUso {

    private final EventoServico servico;
    private final InscricaoRepositorio inscricaoRepositorio;
    private final CarteiraVirtualServico carteiraServico;
    private final AtualizadorEstatisticaListener atualizadorEstatistica;

    public CancelarEventoCasoDeUso(EventoServico servico,
                                   InscricaoRepositorio inscricaoRepositorio,
                                   CarteiraVirtualServico carteiraServico,
                                   AtualizadorEstatisticaListener atualizadorEstatistica) {
        Objects.requireNonNull(servico);
        Objects.requireNonNull(inscricaoRepositorio);
        Objects.requireNonNull(carteiraServico);
        Objects.requireNonNull(atualizadorEstatistica);
        this.servico = servico;
        this.inscricaoRepositorio = inscricaoRepositorio;
        this.carteiraServico = carteiraServico;
        this.atualizadorEstatistica = atualizadorEstatistica;
    }

    public Resultado executar(UUID eventoId) {
        // 1. Cancela o agregado evento (status -> CANCELADO, lote encerra)
        servico.cancelar(new EventoId(eventoId));

        // 2. Reembolsa 100% para cada inscrito ativo
        List<Inscricao> inscricoes = inscricaoRepositorio.buscarPorEventoId(eventoId);
        int reembolsadas = 0;
        BigDecimal totalReembolsado = BigDecimal.ZERO;
        for (Inscricao inscricao : inscricoes) {
            if (inscricao.getStatus() == StatusInscricao.CANCELADA) continue;
            BigDecimal valorIntegral = inscricao.getValorPago();
            carteiraServico.creditar(inscricao.getParticipanteId(), valorIntegral);
            inscricao.cancelar();
            inscricaoRepositorio.salvar(inscricao);
            reembolsadas++;
            totalReembolsado = totalReembolsado.add(valorIntegral);
        }

        // 3. Congela snapshot (RN05)
        atualizadorEstatistica.aoEncerrarEvento(eventoId);

        return new Resultado(reembolsadas, totalReembolsado);
    }

    public record Resultado(int inscricoesReembolsadas, BigDecimal valorTotalReembolsado) {}
}
