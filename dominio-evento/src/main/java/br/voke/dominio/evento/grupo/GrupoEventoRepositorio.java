package br.voke.dominio.evento.grupo;

import java.util.Optional;
import java.util.UUID;

public interface GrupoEventoRepositorio {
    void salvar(GrupoEvento grupo);
    Optional<GrupoEvento> buscarPorId(GrupoEventoId id);
    Optional<GrupoEvento> buscarPorEventoId(UUID eventoId);
    void remover(GrupoEventoId id);
}
