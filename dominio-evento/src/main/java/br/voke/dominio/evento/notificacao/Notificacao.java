package br.voke.dominio.evento.notificacao;

import br.voke.dominio.compartilhado.EntidadeBase;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Notificacao extends EntidadeBase<NotificacaoId> {

    private final UUID eventoId;
    private String conteudo;
    private LocalDateTime dataEnvio;
    private boolean editada;
    private final Set<UUID> destinatariosIds;

    public Notificacao(NotificacaoId id, UUID eventoId, String conteudo) {
        this(id, eventoId, conteudo, Set.of());
    }

    public Notificacao(NotificacaoId id, UUID eventoId, String conteudo, Set<UUID> destinatariosIds) {
        super(id);
        Objects.requireNonNull(eventoId, "Evento é obrigatório");
        Objects.requireNonNull(conteudo, "Conteúdo é obrigatório");
        Objects.requireNonNull(destinatariosIds, "Destinatarios sao obrigatorios");
        this.eventoId = eventoId;
        this.conteudo = conteudo;
        this.dataEnvio = LocalDateTime.now();
        this.editada = false;
        this.destinatariosIds = new HashSet<>(destinatariosIds);
    }

    public void editarConteudo(String novoConteudo) {
        Objects.requireNonNull(novoConteudo, "Conteúdo é obrigatório");
        this.conteudo = novoConteudo;
        this.dataEnvio = LocalDateTime.now();
        this.editada = true;
    }

    public UUID getEventoId() { return eventoId; }
    public String getConteudo() { return conteudo; }
    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public boolean isEditada() { return editada; }
    public Set<UUID> getDestinatariosIds() { return Collections.unmodifiableSet(destinatariosIds); }
    public boolean foiEnviadaPara(UUID participanteId) { return destinatariosIds.contains(participanteId); }
}