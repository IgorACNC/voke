package br.voke.dominio.evento.chat;

import java.util.List;
import java.util.UUID;

/**
 * Decorator que verifica se o solicitante tem acesso ao canal de chat.
 * Se {@code podeAcessar} for {@code false}, lança {@link AcessoChatCanalNegadoException}.
 */
public class AcessoCanalDecorator extends ChatCanalServicoDecorator {

    public AcessoCanalDecorator(ChatCanalServicoInterface decorado) {
        super(decorado);
    }

    @Override
    public MensagemCanal enviar(TipoCanalChat tipo, UUID canalId, UUID remetenteId,
                                String conteudo, boolean podeAcessar) {
        if (!podeAcessar) {
            throw new AcessoChatCanalNegadoException();
        }
        return super.enviar(tipo, canalId, remetenteId, conteudo, podeAcessar);
    }

    @Override
    public List<MensagemCanal> listar(TipoCanalChat tipo, UUID canalId,
                                      UUID solicitanteId, boolean podeAcessar) {
        if (!podeAcessar) {
            throw new AcessoChatCanalNegadoException();
        }
        return super.listar(tipo, canalId, solicitanteId, podeAcessar);
    }
}
