package br.voke.dominio.evento.estatistica;

import java.util.List;
import java.util.UUID;

public abstract class DashboardServicoDecorator implements DashboardServicoInterface {

    protected final DashboardServicoInterface componente;

    protected DashboardServicoDecorator(DashboardServicoInterface componente) {
        this.componente = componente;
    }

    @Override
    public EstatisticaEvento consultarPorEvento(UUID eventoId, UUID solicitanteId, boolean podeAcessar) {
        return componente.consultarPorEvento(eventoId, solicitanteId, podeAcessar);
    }

    @Override
    public List<EstatisticaEvento> consultarOverview(UUID organizadorId) {
        return componente.consultarOverview(organizadorId);
    }
}
