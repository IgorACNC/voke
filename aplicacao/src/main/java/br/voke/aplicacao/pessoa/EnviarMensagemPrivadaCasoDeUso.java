package br.voke.aplicacao.pessoa;

import br.voke.dominio.pessoa.chat.ChatPrivadoServico;
import br.voke.dominio.pessoa.chat.MensagemPrivada;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.Objects;
import java.util.UUID;

public class EnviarMensagemPrivadaCasoDeUso {

    private final ChatPrivadoServico servico;

    public EnviarMensagemPrivadaCasoDeUso(ChatPrivadoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public MensagemPrivada executar(UUID remetenteId, UUID destinatarioId, String conteudo) {
        return servico.enviar(new ParticipanteId(remetenteId), new ParticipanteId(destinatarioId), conteudo);
    }
}
