package br.voke.infraestrutura.evento.notificacao;

import org.springframework.stereotype.Repository;
import br.voke.dominio.evento.notificacao.Notificacao;
import br.voke.dominio.evento.notificacao.NotificacaoId;
import br.voke.dominio.evento.notificacao.NotificacaoRepositorio;
import br.voke.dominio.evento.notificacao.StatusNotificacao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class NotificacaoRepositorioJpa implements NotificacaoRepositorio {

    private final SpringNotificacaoRepository repository;

    public NotificacaoRepositorioJpa(SpringNotificacaoRepository repository) {
        this.repository = repository;
    }

    public void salvar(Notificacao notificacao) {
        repository.save(NotificacaoJpaMapper.paraJpa(notificacao));
    }

    public Optional<Notificacao> buscarPorId(NotificacaoId id) {
        return repository.findById(id.getValor()).map(NotificacaoJpaMapper::paraDominio);
    }

    public List<Notificacao> buscarPorEventoId(UUID eventoId) {
        return repository.findByEventoId(eventoId).stream().map(NotificacaoJpaMapper::paraDominio).toList();
    }

    public List<Notificacao> buscarPorParticipanteId(UUID participanteId) {
        return repository.findByParticipanteId(participanteId).stream().map(NotificacaoJpaMapper::paraDominio).toList();
    }

    public List<Notificacao> buscarAgendadasAteDataHora(LocalDateTime dataHora) {
        return repository
                .findByStatusAndDataAgendamentoLessThanEqual(StatusNotificacao.AGENDADA, dataHora)
                .stream()
                .map(NotificacaoJpaMapper::paraDominio)
                .toList();
    }

    public void remover(NotificacaoId id) {
        repository.deleteById(id.getValor());
    }
}
