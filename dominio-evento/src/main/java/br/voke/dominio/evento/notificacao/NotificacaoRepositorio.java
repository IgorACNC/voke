package br.voke.dominio.evento.notificacao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificacaoRepositorio {
    void salvar(Notificacao notificacao);
    Optional<Notificacao> buscarPorId(NotificacaoId id);
    List<Notificacao> buscarPorEventoId(UUID eventoId);
    void remover(NotificacaoId id);
}
