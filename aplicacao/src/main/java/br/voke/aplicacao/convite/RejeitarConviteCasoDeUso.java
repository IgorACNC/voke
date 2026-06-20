package br.voke.aplicacao.convite;

import br.voke.dominio.inscricao.convite.ConviteId;
import br.voke.dominio.inscricao.convite.ConviteServico;

import java.util.Objects;
import java.util.UUID;

public class RejeitarConviteCasoDeUso {

    private final ConviteServico servico;

    public RejeitarConviteCasoDeUso(ConviteServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID conviteId, UUID destinatarioId) {
        servico.rejeitar(new ConviteId(conviteId), destinatarioId);
    }
}
