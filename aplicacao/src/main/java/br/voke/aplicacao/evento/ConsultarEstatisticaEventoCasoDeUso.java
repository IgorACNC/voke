package br.voke.aplicacao.evento;

import br.voke.dominio.evento.estatistica.DashboardServicoInterface;
import br.voke.dominio.evento.estatistica.EstatisticaEvento;

import java.util.Objects;
import java.util.UUID;

public class ConsultarEstatisticaEventoCasoDeUso {

    private final DashboardServicoInterface dashboard;

    public ConsultarEstatisticaEventoCasoDeUso(DashboardServicoInterface dashboard) {
        Objects.requireNonNull(dashboard);
        this.dashboard = dashboard;
    }

    public EstatisticaEvento executar(UUID eventoId, UUID solicitanteId, boolean podeAcessar) {
        Objects.requireNonNull(eventoId, "eventoId e obrigatorio");
        Objects.requireNonNull(solicitanteId, "solicitanteId e obrigatorio");
        return dashboard.consultarPorEvento(eventoId, solicitanteId, podeAcessar);
    }
}
