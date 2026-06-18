package br.voke.dominio.evento.estatistica;

import java.util.List;
import java.util.UUID;

public class DashboardServico implements DashboardServicoInterface {

    private final EstatisticaEventoRepositorio repositorio;

    public DashboardServico(EstatisticaEventoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public EstatisticaEvento consultarPorEvento(UUID eventoId, UUID solicitanteId, boolean podeAcessar) {
        return repositorio.buscarPorEventoId(eventoId)
            .orElseThrow(() -> new IllegalArgumentException("Estatistica nao encontrada para evento " + eventoId));
    }

    @Override
    public List<EstatisticaEvento> consultarOverview(UUID organizadorId) {
        return repositorio.listarPorOrganizador(organizadorId);
    }
}
