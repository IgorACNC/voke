package br.voke.dominio.evento.chat;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Decorator base abstrato (Decorator) no padrão GoF. Delega todas as operações
 * ao componente decorado, permitindo que subclasses sobrescrevam apenas os
 * métodos relevantes.
 */
public abstract class ChatCanalServicoDecorator implements ChatCanalServicoInterface {

    protected final ChatCanalServicoInterface decorado;

    protected ChatCanalServicoDecorator(ChatCanalServicoInterface decorado) {
        Objects.requireNonNull(decorado, "Componente decorado e obrigatorio");
        this.decorado = decorado;
    }

    @Override
    public MensagemCanal enviar(TipoCanalChat tipo, UUID canalId, UUID remetenteId,
                                String conteudo, boolean podeAcessar) {
        return decorado.enviar(tipo, canalId, remetenteId, conteudo, podeAcessar);
    }

    @Override
    public List<MensagemCanal> listar(TipoCanalChat tipo, UUID canalId,
                                      UUID solicitanteId, boolean podeAcessar) {
        return decorado.listar(tipo, canalId, solicitanteId, podeAcessar);
    }
}
