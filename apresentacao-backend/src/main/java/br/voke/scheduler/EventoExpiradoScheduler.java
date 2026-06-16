package br.voke.scheduler;

import br.voke.aplicacao.evento.EncerrarEventosExpiradosCasoDeUso;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EventoExpiradoScheduler {

    private static final Logger log = LoggerFactory.getLogger(EventoExpiradoScheduler.class);
    private static final long VINTE_MINUTOS_EM_MS = 20L * 60L * 1000L;
    private static final long UM_MINUTO_EM_MS = 60L * 1000L;

    private final EncerrarEventosExpiradosCasoDeUso encerrarEventosExpirados;

    public EventoExpiradoScheduler(EncerrarEventosExpiradosCasoDeUso encerrarEventosExpirados) {
        this.encerrarEventosExpirados = encerrarEventosExpirados;
    }

    @Scheduled(fixedRate = VINTE_MINUTOS_EM_MS, initialDelay = UM_MINUTO_EM_MS)
    public void encerrarEventosExpirados() {
        int quantidade = encerrarEventosExpirados.executar();
        if (quantidade > 0) {
            log.info("Encerrados {} eventos expirados", quantidade);
        }
    }
}
