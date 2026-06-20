package br.voke.agendamento;

import br.voke.dominio.evento.notificacao.Notificacao;
import br.voke.dominio.evento.notificacao.NotificacaoServico;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificacaoScheduler {

    private final NotificacaoServico servico;

    public NotificacaoScheduler(NotificacaoServico servico) {
        this.servico = servico;
    }

    @Scheduled(fixedDelay = 60_000)
    public void processarNotificacoesAgendadas() {
        List<Notificacao> processadas = servico.processarAgendadas();
        if (!processadas.isEmpty()) {
            System.out.printf("[Scheduler] %d notificações agendadas foram enviadas%n", processadas.size());
        }
    }
}
