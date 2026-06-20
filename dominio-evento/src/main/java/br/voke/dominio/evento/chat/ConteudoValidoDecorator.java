package br.voke.dominio.evento.chat;

import java.util.UUID;

/**
 * Decorator que pré-valida o conteúdo da mensagem antes de chegar ao agregado,
 * oferecendo mensagens de erro mais amigáveis.
 */
public class ConteudoValidoDecorator extends ChatCanalServicoDecorator {

    private static final int LIMITE_CARACTERES = 1000;

    public ConteudoValidoDecorator(ChatCanalServicoInterface decorado) {
        super(decorado);
    }

    @Override
    public MensagemCanal enviar(TipoCanalChat tipo, UUID canalId, UUID remetenteId,
                                String conteudo, boolean podeAcessar) {
        if (conteudo == null || conteudo.trim().isEmpty()) {
            throw new ConteudoMensagemInvalidoException("O conteudo da mensagem nao pode ser vazio");
        }
        if (conteudo.trim().length() > LIMITE_CARACTERES) {
            throw new ConteudoMensagemInvalidoException(
                    "A mensagem excede o limite de " + LIMITE_CARACTERES + " caracteres");
        }
        return super.enviar(tipo, canalId, remetenteId, conteudo, podeAcessar);
    }
}
