package br.voke.aplicacao.evento;

import br.voke.dominio.evento.estatistica.DashboardServicoInterface;
import br.voke.dominio.evento.estatistica.EstatisticaEvento;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ConsultarOverviewOrganizadorCasoDeUso {

    private final DashboardServicoInterface dashboard;

    public ConsultarOverviewOrganizadorCasoDeUso(DashboardServicoInterface dashboard) {
        Objects.requireNonNull(dashboard);
        this.dashboard = dashboard;
    }

    public List<EstatisticaEvento> executar(UUID organizadorId) {
        Objects.requireNonNull(organizadorId, "organizadorId e obrigatorio");
        return dashboard.consultarOverview(organizadorId);
    }
}
