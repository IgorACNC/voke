package br.voke.agendamento;

import br.voke.aplicacao.fidelidade.ExpirarSugestoesAntigasCasoDeUso;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SugestaoScheduler {

    private final ExpirarSugestoesAntigasCasoDeUso expirarSugestoes;

    public SugestaoScheduler(ExpirarSugestoesAntigasCasoDeUso expirarSugestoes) {
        this.expirarSugestoes = expirarSugestoes;
    }

    @Scheduled(cron = "0 0 3 * * MON")
    public void expirarSugestoesSemanais() {
        int quantidade = expirarSugestoes.executar();
        System.out.printf("[Scheduler] %d sugestões expiradas no ciclo semanal%n", quantidade);
    }
}
