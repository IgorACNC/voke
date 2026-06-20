package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.pontos.ContaPontos;
import br.voke.dominio.fidelidade.pontos.ContaPontosRepositorio;
import br.voke.dominio.fidelidade.pontos.ContaPontosServico;
import br.voke.dominio.fidelidade.pontos.TipoTransacaoPontos;
import br.voke.dominio.fidelidade.pontos.TransacaoPontosRepositorio;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * RN4 - varre as contas de pontos e expira (FIFO) os ganhos cuja data
 * de aquisição seja anterior à janela de validade.
 *
 * Algoritmo FIFO via consultas:
 *   ganhosVencidos = SUM(GANHO_PRESENCA) cuja dataHora < cutoff
 *   saidas         = SUM(RESGATE_RECOMPENSA) + SUM(EXPIRACAO)
 *   aExpirar       = max(0, ganhosVencidos - saidas)
 *
 * Como saídas consomem cronologicamente os ganhos mais antigos primeiro,
 * o saldo de ganhos vencidos que ainda não foi consumido é exatamente
 * ganhosVencidos - saidas. Quando aExpirar > 0, debita esse valor e
 * grava uma TransacaoPontos do tipo EXPIRACAO (alimenta `saidas` na
 * próxima execução, evitando dupla expiração).
 */
public class ExpirarPontosVencidosCasoDeUso {

    public static final int DIAS_VALIDADE = 30;

    private final ContaPontosRepositorio contaRepositorio;
    private final TransacaoPontosRepositorio transacaoRepositorio;
    private final ContaPontosServico servico;

    public ExpirarPontosVencidosCasoDeUso(ContaPontosRepositorio contaRepositorio,
                                           TransacaoPontosRepositorio transacaoRepositorio,
                                           ContaPontosServico servico) {
        Objects.requireNonNull(contaRepositorio);
        Objects.requireNonNull(transacaoRepositorio);
        Objects.requireNonNull(servico);
        this.contaRepositorio = contaRepositorio;
        this.transacaoRepositorio = transacaoRepositorio;
        this.servico = servico;
    }

    /** Executa a varredura para todos os participantes e retorna o total expirado. */
    public Resultado executar() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(DIAS_VALIDADE);
        int contasAfetadas = 0;
        int pontosTotalExpirados = 0;
        for (ContaPontos conta : contaRepositorio.listarTodas()) {
            int expirados = executarParticipante(conta.getParticipanteId(), cutoff);
            if (expirados > 0) {
                contasAfetadas++;
                pontosTotalExpirados += expirados;
            }
        }
        return new Resultado(contasAfetadas, pontosTotalExpirados);
    }

    /** Executa para um participante específico (útil em testes). */
    public int executarParticipante(UUID participanteId) {
        return executarParticipante(participanteId, LocalDateTime.now().minusDays(DIAS_VALIDADE));
    }

    private int executarParticipante(UUID participanteId, LocalDateTime cutoff) {
        int ganhosVencidos = transacaoRepositorio.somarPontosPorTipoAteData(
                participanteId, TipoTransacaoPontos.GANHO_PRESENCA, cutoff);
        if (ganhosVencidos <= 0) return 0;
        int resgates = transacaoRepositorio.somarPontosPorTipo(
                participanteId, TipoTransacaoPontos.RESGATE_RECOMPENSA);
        int jaExpirados = transacaoRepositorio.somarPontosPorTipo(
                participanteId, TipoTransacaoPontos.EXPIRACAO);
        int aExpirar = ganhosVencidos - resgates - jaExpirados;
        if (aExpirar <= 0) return 0;
        servico.expirarPontos(participanteId, aExpirar);
        return aExpirar;
    }

    public record Resultado(int contasAfetadas, int pontosExpirados) {}
}
