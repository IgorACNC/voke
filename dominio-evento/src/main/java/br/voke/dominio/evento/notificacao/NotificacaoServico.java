package br.voke.dominio.evento.notificacao;

import br.voke.dominio.evento.excecao.EventoCanceladoException;
import br.voke.dominio.evento.excecao.SemDestinatariosException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class NotificacaoServico {

    private final NotificacaoRepositorio repositorio;

    public NotificacaoServico(NotificacaoRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
    }

    public Notificacao enviar(UUID eventoId, String conteudo, boolean eventoAtivo) {
        return enviar(eventoId, conteudo, eventoAtivo, Set.of());
    }

    public Notificacao enviar(UUID eventoId, String conteudo, boolean eventoAtivo, Set<UUID> destinatariosIds) {
        if (!eventoAtivo) {
            throw new EventoCanceladoException("Não é possível enviar notificações para eventos cancelados");
        }
        Notificacao notificacao = new Notificacao(NotificacaoId.novo(), eventoId, conteudo, destinatariosIds);
        repositorio.salvar(notificacao);
        return notificacao;
    }

    /**
     * Envia uma notificação utilizando o padrão Iterator (GoF) para percorrer
     * apenas os participantes elegíveis conforme a RN 1 (Público-Alvo Restrito).
     *
     * <p>O {@link ParticipanteElegivel} encapsula a lógica de filtragem,
     * garantindo que apenas inscrições ativas sejam incluídas como destinatários,
     * blindando este serviço contra detalhes de consultas ou estruturas internas.</p>
     *
     * @param eventoId    identificador do evento
     * @param conteudo    conteúdo da notificação
     * @param eventoAtivo indica se o evento está ativo
     * @param elegiveis   agregado do Iterator que fornece os participantes elegíveis
     * @return a notificação criada e persistida
     */
    public Notificacao enviar(UUID eventoId, String conteudo, boolean eventoAtivo, ParticipanteElegivel elegiveis) {
        Objects.requireNonNull(elegiveis, "Coleção de participantes elegíveis é obrigatória");
        Set<UUID> destinatarios = new HashSet<>();
        for (UUID participanteId : elegiveis) {
            destinatarios.add(participanteId);
        }
        return enviar(eventoId, conteudo, eventoAtivo, destinatarios);
    }

    /**
     * Envia uma notificação segmentada utilizando o padrão Strategy (GoF)
     * composto com o Iterator existente.
     *
     * <p>O Iterator fornece todos os participantes elegíveis (inscrição ativa),
     * e o CriterioSegmentacao filtra esse conjunto conforme a estratégia escolhida
     * pelo organizador (por grupo, por lote ou todos).</p>
     *
     * @param eventoId    identificador do evento
     * @param conteudo    conteúdo da notificação
     * @param eventoAtivo indica se o evento está ativo
     * @param elegiveis   agregado do Iterator com os participantes elegíveis
     * @param criterio    estratégia de segmentação a aplicar
     * @return a notificação criada e persistida
     */
    public Notificacao enviarSegmentada(UUID eventoId, String conteudo, boolean eventoAtivo,
                                        ParticipanteElegivel elegiveis, CriterioSegmentacao criterio) {
        Objects.requireNonNull(elegiveis, "Coleção de participantes elegíveis é obrigatória");
        Objects.requireNonNull(criterio, "Critério de segmentação é obrigatório");

        Set<UUID> todosElegiveis = new HashSet<>();
        for (UUID participanteId : elegiveis) {
            todosElegiveis.add(participanteId);
        }

        Set<UUID> destinatariosFiltrados = criterio.filtrar(todosElegiveis);
        if (destinatariosFiltrados.isEmpty()) {
            throw new SemDestinatariosException("Nenhum destinatário elegível para o critério selecionado");
        }

        if (!eventoAtivo) {
            throw new EventoCanceladoException("Não é possível enviar notificações para eventos cancelados");
        }

        Notificacao notificacao = new Notificacao(NotificacaoId.novo(), eventoId, conteudo, destinatariosFiltrados);
        notificacao.definirCriterioSegmentacao(criterio.descricao());
        repositorio.salvar(notificacao);
        return notificacao;
    }

    /**
     * Agenda uma notificação para envio futuro.
     *
     * @param eventoId        identificador do evento
     * @param conteudo        conteúdo da notificação
     * @param eventoAtivo     indica se o evento está ativo
     * @param dataAgendamento data e hora programada para envio
     * @return a notificação criada com status AGENDADA
     */
    public Notificacao agendar(UUID eventoId, String conteudo, boolean eventoAtivo, LocalDateTime dataAgendamento) {
        if (!eventoAtivo) {
            throw new EventoCanceladoException("Não é possível enviar notificações para eventos cancelados");
        }
        Notificacao notificacao = Notificacao.criarRascunho(NotificacaoId.novo(), eventoId, conteudo);
        notificacao.agendar(dataAgendamento);
        repositorio.salvar(notificacao);
        return notificacao;
    }

    /**
     * Cancela uma notificação agendada antes do envio.
     *
     * @param id identificador da notificação
     */
    public void cancelarAgendada(NotificacaoId id) {
        Notificacao notificacao = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificação não encontrada"));
        notificacao.cancelar();
        repositorio.salvar(notificacao);
    }

    /**
     * Processa todas as notificações agendadas cuja data de envio já chegou.
     *
     * @return lista de notificações que foram processadas e enviadas
     */
    public List<Notificacao> processarAgendadas() {
        List<Notificacao> agendadas = repositorio.buscarAgendadasAteDataHora(LocalDateTime.now());
        for (Notificacao notificacao : agendadas) {
            notificacao.processarEnvio();
            repositorio.salvar(notificacao);
        }
        return agendadas;
    }

    public List<Notificacao> listarPorParticipante(UUID participanteId) {
        return repositorio.buscarPorParticipanteId(participanteId);
    }

    public void editar(NotificacaoId id, String novoConteudo) {
        Notificacao notificacao = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificação não encontrada"));
        notificacao.editarConteudo(novoConteudo);
        repositorio.salvar(notificacao);
    }

    public void remover(NotificacaoId id) {
        repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificação não encontrada"));
        repositorio.remover(id);
    }
}