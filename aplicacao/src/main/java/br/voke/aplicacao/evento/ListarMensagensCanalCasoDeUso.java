package br.voke.aplicacao.evento;

import br.voke.dominio.evento.chat.ChatCanalServicoInterface;
import br.voke.dominio.evento.chat.MensagemCanal;
import br.voke.dominio.evento.chat.TipoCanalChat;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListarMensagensCanalCasoDeUso {

    private final ChatCanalServicoInterface servico;

    public ListarMensagensCanalCasoDeUso(ChatCanalServicoInterface servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public List<MensagemCanal> executar(TipoCanalChat tipo, UUID canalId,
                                        UUID solicitanteId, boolean podeAcessar) {
        return servico.listar(tipo, canalId, solicitanteId, podeAcessar);
    }
}
