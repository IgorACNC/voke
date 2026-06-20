package br.voke.dominio.evento.notificacao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificacaoRepositorio {
    void salvar(Notificacao notificacao);
    Optional<Notificacao> buscarPorId(NotificacaoId id);
    List<Notificacao> buscarPorEventoId(UUID eventoId);
    List<Notificacao> buscarPorParticipanteId(UUID participanteId);
    List<Notificacao> buscarAgendadasAteDataHora(LocalDateTime dataHora);
    void remover(NotificacaoId id);
}