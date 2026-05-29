package br.voke.dominio.evento.notificacao;

import br.voke.dominio.evento.excecao.EventoCanceladoException;

import java.util.HashSet;
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

    public java.util.List<Notificacao> listarPorParticipante(UUID participanteId) {
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