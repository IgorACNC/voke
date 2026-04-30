package br.voke.dominio.pessoa.amizade;

import br.voke.dominio.compartilhado.EntidadeBase;
import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class ComunidadeAmigos extends EntidadeBase<ComunidadeAmigosId> {

    private NomeCompleto nome;
    private final ParticipanteId criadorId;
    private final Set<ParticipanteId> membros;
    private final Set<UUID> eventoCompartilhadoIds;

    public ComunidadeAmigos(ComunidadeAmigosId id, NomeCompleto nome, ParticipanteId criadorId) {
        super(id);
        Objects.requireNonNull(nome, "Nome da comunidade é obrigatório");
        Objects.requireNonNull(criadorId, "Criador é obrigatório");
        this.nome = nome;
        this.criadorId = criadorId;
        this.membros = new HashSet<>();
        this.eventoCompartilhadoIds = new HashSet<>();
        this.membros.add(criadorId);
    }

    public void adicionarMembro(ParticipanteId participanteId) {
        membros.add(participanteId);
    }

    public void removerMembro(ParticipanteId participanteId) {
        membros.remove(participanteId);
    }

    public void compartilharEvento(UUID eventoId) {
        Objects.requireNonNull(eventoId, "Evento é obrigatório");
        eventoCompartilhadoIds.add(eventoId);
    }

    public NomeCompleto getNome() { return nome; }
    public ParticipanteId getCriadorId() { return criadorId; }
    public Set<ParticipanteId> getMembros() { return Collections.unmodifiableSet(membros); }
    public Set<UUID> getEventoCompartilhadoIds() { return Collections.unmodifiableSet(eventoCompartilhadoIds); }
}
