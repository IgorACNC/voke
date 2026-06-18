package br.voke.dominio.evento.estatistica;

import java.util.UUID;

public class PrivilegioOrganizadorDashboardDecorator extends DashboardServicoDecorator {

    public PrivilegioOrganizadorDashboardDecorator(DashboardServicoInterface componente) {
        super(componente);
    }

    @Override
    public EstatisticaEvento consultarPorEvento(UUID eventoId, UUID solicitanteId, boolean podeAcessar) {
        if (!podeAcessar) {
            throw new AcessoDashboardNegadoException();
        }
        return super.consultarPorEvento(eventoId, solicitanteId, podeAcessar);
    }
}
