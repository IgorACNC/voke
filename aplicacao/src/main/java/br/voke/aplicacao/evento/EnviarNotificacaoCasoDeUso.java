package br.voke.aplicacao.evento;

import br.voke.dominio.evento.notificacao.Notificacao;
import br.voke.dominio.evento.notificacao.NotificacaoServico;
import br.voke.dominio.evento.notificacao.ParticipanteElegivel;

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

    /**
     * Executa o envio de notificação utilizando o padrão Iterator (GoF)
     * para filtrar automaticamente os destinatários elegíveis (RN 1).
     *
     * @param eventoId    identificador do evento
     * @param conteudo    conteúdo da notificação
     * @param eventoAtivo indica se o evento está ativo
     * @param elegiveis   agregado do Iterator com os participantes elegíveis
     * @return a notificação criada
     */
    public Notificacao executar(UUID eventoId, String conteudo, boolean eventoAtivo, ParticipanteElegivel elegiveis) {
        return servico.enviar(eventoId, conteudo, eventoAtivo, elegiveis);
    }
}
