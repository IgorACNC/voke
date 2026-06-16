package br.voke.dominio.evento.chat;

import java.util.List;
import java.util.UUID;

/**
 * Contrato do Componente (Component) no padrão Decorator para o serviço de
 * chat em canais de grupo/subgrupo. Tanto o serviço concreto quanto os
 * decorators implementam esta interface.
 *
 * A flag {@code podeAcessar} é calculada no controller e injetada aqui.
 * O domínio apenas valida que o acesso foi autorizado.
 */
public interface ChatCanalServicoInterface {

    MensagemCanal enviar(TipoCanalChat tipo, UUID canalId, UUID remetenteId,
                         String conteudo, boolean podeAcessar);

    List<MensagemCanal> listar(TipoCanalChat tipo, UUID canalId,
                               UUID solicitanteId, boolean podeAcessar);
}
