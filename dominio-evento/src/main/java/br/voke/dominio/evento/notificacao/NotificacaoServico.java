package br.voke.dominio.evento.notificacao;

import br.voke.dominio.evento.excecao.EventoCanceladoException;

import java.util.Objects;
import java.util.UUID;

public class NotificacaoServico {

    private final NotificacaoRepositorio repositorio;

    public NotificacaoServico(NotificacaoRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
    }

    public Notificacao enviar(UUID eventoId, String conteudo, boolean eventoAtivo) {
        if (!eventoAtivo) {
            throw new EventoCanceladoException("Não é possível enviar notificações para eventos cancelados");
        }
        Notificacao notificacao = new Notificacao(NotificacaoId.novo(), eventoId, conteudo);
        repositorio.salvar(notificacao);
        return notificacao;
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
