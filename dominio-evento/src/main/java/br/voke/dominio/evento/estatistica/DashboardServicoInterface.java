package br.voke.dominio.evento.estatistica;

import java.util.List;
import java.util.UUID;

public interface DashboardServicoInterface {
    EstatisticaEvento consultarPorEvento(UUID eventoId, UUID solicitanteId, boolean podeAcessar);
    List<EstatisticaEvento> consultarOverview(UUID organizadorId);
}
