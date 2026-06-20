package br.voke.agendamento;

import br.voke.aplicacao.fidelidade.ResetarLimitesDiariosCasoDeUso;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CarteiraScheduler {

    private final ResetarLimitesDiariosCasoDeUso resetarLimites;

    public CarteiraScheduler(ResetarLimitesDiariosCasoDeUso resetarLimites) {
        this.resetarLimites = resetarLimites;
    }

    // Executa à meia-noite todos os dias para zerar totalInseridoHoje e contadorSaquesHoje
    @Scheduled(cron = "0 0 0 * * *")
    public void resetarLimitesDiarios() {
        resetarLimites.executar();
        System.out.println("[CarteiraScheduler] Limites diários resetados.");
    }
}
