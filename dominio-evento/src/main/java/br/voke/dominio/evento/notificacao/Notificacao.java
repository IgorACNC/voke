package br.voke.dominio.evento.notificacao;

import br.voke.dominio.compartilhado.EntidadeBase;
import br.voke.dominio.evento.excecao.AgendamentoInvalidoException;
import br.voke.dominio.evento.excecao.LimiteEdicoesAtingidoException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Notificacao extends EntidadeBase<NotificacaoId> {

    public static final int LIMITE_MAXIMO_EDICOES = 3;

    private final UUID eventoId;
    private String conteudo;
    private LocalDateTime dataEnvio;
    private boolean editada;
    private final Set<UUID> destinatariosIds;
    private StatusNotificacao status;
    private LocalDateTime dataAgendamento;
    private int contadorEdicoes;
    private String criterioSegmentacao;

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
        this.status = StatusNotificacao.ENVIADA;
        this.dataAgendamento = null;
        this.contadorEdicoes = 0;
        this.criterioSegmentacao = null;
    }

    public void editarConteudo(String novoConteudo) {
        Objects.requireNonNull(novoConteudo, "Conteúdo é obrigatório");
        if (status == StatusNotificacao.CANCELADA) {
            throw new IllegalStateException("Não é possível editar uma notificação cancelada");
        }
        if (contadorEdicoes >= LIMITE_MAXIMO_EDICOES) {
            throw new LimiteEdicoesAtingidoException("Limite de edições atingido para esta notificação");
        }
        this.conteudo = novoConteudo;
        this.dataEnvio = LocalDateTime.now();
        this.editada = true;
        this.contadorEdicoes++;
    }

    /**
     * Agenda o envio da notificação para uma data futura.
     * A notificação passa do status RASCUNHO para AGENDADA.
     *
     * @param dataAgendamento data e hora programada para envio
     */
    public void agendar(LocalDateTime dataAgendamento) {
        Objects.requireNonNull(dataAgendamento, "Data de agendamento é obrigatória");
        if (!dataAgendamento.isAfter(LocalDateTime.now())) {
            throw new AgendamentoInvalidoException("A data de agendamento deve ser no futuro");
        }
        this.dataAgendamento = dataAgendamento;
        this.status = StatusNotificacao.AGENDADA;
    }

    /**
     * Cancela uma notificação agendada antes que seja enviada.
     */
    public void cancelar() {
        if (status != StatusNotificacao.AGENDADA) {
            throw new IllegalStateException("Apenas notificações agendadas podem ser canceladas");
        }
        this.status = StatusNotificacao.CANCELADA;
    }

    /**
     * Processa o envio de uma notificação agendada.
     * Muda o status de AGENDADA para ENVIADA e registra a data de envio.
     */
    public void processarEnvio() {
        if (status != StatusNotificacao.AGENDADA) {
            throw new IllegalStateException("Apenas notificações agendadas podem ser processadas");
        }
        this.status = StatusNotificacao.ENVIADA;
        this.dataEnvio = LocalDateTime.now();
    }

    /**
     * Cria uma notificação com status RASCUNHO, sem envio imediato.
     */
    public static Notificacao criarRascunho(NotificacaoId id, UUID eventoId, String conteudo) {
        Notificacao notificacao = new Notificacao(id, eventoId, conteudo);
        notificacao.status = StatusNotificacao.RASCUNHO;
        return notificacao;
    }

    public void definirCriterioSegmentacao(String criterio) {
        this.criterioSegmentacao = criterio;
    }

    public UUID getEventoId() { return eventoId; }
    public String getConteudo() { return conteudo; }
    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public boolean isEditada() { return editada; }
    public Set<UUID> getDestinatariosIds() { return Collections.unmodifiableSet(destinatariosIds); }
    public boolean foiEnviadaPara(UUID participanteId) { return destinatariosIds.contains(participanteId); }
    public StatusNotificacao getStatus() { return status; }
    public LocalDateTime getDataAgendamento() { return dataAgendamento; }
    public int getContadorEdicoes() { return contadorEdicoes; }
    public String getCriterioSegmentacao() { return criterioSegmentacao; }
}