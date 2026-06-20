package br.voke.dominio.fidelidade.sugestao;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface EventoConsultaGateway {
    Set<UUID> buscarCategoriasDoEvento(UUID eventoId);
    boolean eventoEstaDisponivel(UUID eventoId);
    List<EventoCandidato> buscarEventosCandidatosPorCategorias(Set<UUID> categoriaIds);

    record EventoCandidato(UUID eventoId, Set<UUID> categoriaIds) {}
}
