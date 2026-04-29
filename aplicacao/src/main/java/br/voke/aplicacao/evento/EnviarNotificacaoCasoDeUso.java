package br.voke.aplicacao.evento;

import br.voke.dominio.evento.notificacao.Notificacao;
import br.voke.dominio.evento.notificacao.NotificacaoServico;

import java.util.Objects;
import java.util.UUID;

public class EnviarNotificacaoCasoDeUso {

    private final NotificacaoServico servico;

    public EnviarNotificacaoCasoDeUso(NotificacaoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public Notificacao executar(UUID eventoId, String conteudo, boolean eventoAtivo) {
        return servico.enviar(eventoId, conteudo, eventoAtivo);
    }
}
