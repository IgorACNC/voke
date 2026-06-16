package br.voke.aplicacao.evento;

import br.voke.dominio.evento.chat.ChatCanalServicoInterface;
import br.voke.dominio.evento.chat.MensagemCanal;
import br.voke.dominio.evento.chat.TipoCanalChat;

import java.util.Objects;
import java.util.UUID;

public class EnviarMensagemCanalCasoDeUso {

    private final ChatCanalServicoInterface servico;

    public EnviarMensagemCanalCasoDeUso(ChatCanalServicoInterface servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public MensagemCanal executar(TipoCanalChat tipo, UUID canalId, UUID remetenteId,
                                  String conteudo, boolean podeAcessar) {
        return servico.enviar(tipo, canalId, remetenteId, conteudo, podeAcessar);
    }
}
