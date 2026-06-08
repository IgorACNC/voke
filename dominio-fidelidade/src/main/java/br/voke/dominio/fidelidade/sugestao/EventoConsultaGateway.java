package br.voke.dominio.fidelidade.sugestao;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface EventoConsultaGateway {
    Set<String> buscarTagsDoEvento(UUID eventoId);
    boolean eventoEstaDisponivel(UUID eventoId);
    List<EventoCandidato> buscarEventosCandidatosPorTags(Set<String> tags);

    record EventoCandidato(UUID eventoId, Set<String> tags) {}
}
