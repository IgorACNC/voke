package br.voke.dominio.evento.notificacao;

import br.voke.dominio.compartilhado.EntidadeBase;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Notificacao extends EntidadeBase<NotificacaoId> {

    private final UUID eventoId;
    private String conteudo;
    private LocalDateTime dataEnvio;
    private boolean editada;

    public Notificacao(NotificacaoId id, UUID eventoId, String conteudo) {
        super(id);
        Objects.requireNonNull(eventoId, "Evento é obrigatório");
        Objects.requireNonNull(conteudo, "Conteúdo é obrigatório");
        this.eventoId = eventoId;
        this.conteudo = conteudo;
        this.dataEnvio = LocalDateTime.now();
        this.editada = false;
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
}
