package br.voke.agendamento;

import br.voke.aplicacao.fidelidade.ExpirarPontosVencidosCasoDeUso;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * RN4 - executa a expiração de pontos vencidos uma vez por dia,
 * todos os dias às 03:00. Janela escolhida fora do horário de pico
 * para minimizar contenção no banco.
 */
@Component
public class PontosExpiracaoScheduler {

    private static final Logger log = LoggerFactory.getLogger(PontosExpiracaoScheduler.class);

    private final ExpirarPontosVencidosCasoDeUso expirar;

    public PontosExpiracaoScheduler(ExpirarPontosVencidosCasoDeUso expirar) {
        this.expirar = expirar;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void executar() {
        ExpirarPontosVencidosCasoDeUso.Resultado r = expirar.executar();
        if (r.contasAfetadas() > 0) {
            log.info("[PontosExpiracaoScheduler] {} contas afetadas, {} pontos expirados",
                    r.contasAfetadas(), r.pontosExpirados());
        }
    }
}
