package br.voke.aplicacao.pessoa;

import br.voke.dominio.pessoa.chat.ChatPrivadoServico;
import br.voke.dominio.pessoa.chat.MensagemPrivada;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListarMensagensPrivadasCasoDeUso {

    private final ChatPrivadoServico servico;

    public ListarMensagensPrivadasCasoDeUso(ChatPrivadoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public List<MensagemPrivada> executar(UUID participanteId, UUID amigoId) {
        return servico.listarConversa(new ParticipanteId(participanteId), new ParticipanteId(amigoId));
    }
}
