package br.voke.dominio.evento.estatistica;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstatisticaEventoRepositorio {
    void salvar(EstatisticaEvento estatistica);
    Optional<EstatisticaEvento> buscarPorEventoId(UUID eventoId);
    List<EstatisticaEvento> listarPorOrganizador(UUID organizadorId);
}
