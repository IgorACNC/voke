package br.voke.dominio.evento.grupo;

import br.voke.dominio.compartilhado.EntidadeBase;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class GrupoEvento extends EntidadeBase<GrupoEventoId> {

    private String nome;
    private String regras;
    private final UUID eventoId;
    private final UUID organizadorId;
    private final Set<UUID> membrosIds;

    public GrupoEvento(GrupoEventoId id, String nome, String regras, UUID eventoId, UUID organizadorId) {
        super(id);
        Objects.requireNonNull(nome, "Nome do grupo é obrigatório");
        Objects.requireNonNull(eventoId, "Evento é obrigatório");
        Objects.requireNonNull(organizadorId, "Organizador é obrigatório");
        this.nome = nome;
        this.regras = regras;
        this.eventoId = eventoId;
        this.organizadorId = organizadorId;
        this.membrosIds = new HashSet<>();
    }

    public void adicionarMembro(UUID participanteId) {
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        membrosIds.add(participanteId);
    }

    public void removerMembro(UUID participanteId) {
        membrosIds.remove(participanteId);
    }

    public void atualizarRegras(String novasRegras) {
        this.regras = novasRegras;
    }

    public String getNome() { return nome; }
    public String getRegras() { return regras; }
    public UUID getEventoId() { return eventoId; }
    public UUID getOrganizadorId() { return organizadorId; }
    public Set<UUID> getMembrosIds() { return Collections.unmodifiableSet(membrosIds); }
}
