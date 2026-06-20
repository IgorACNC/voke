package br.voke.aplicacao.convite;

import br.voke.dominio.inscricao.convite.ConviteId;
import br.voke.dominio.inscricao.convite.ConviteServico;

import java.util.Objects;
import java.util.UUID;

public class CancelarConviteCasoDeUso {

    private final ConviteServico servico;

    public CancelarConviteCasoDeUso(ConviteServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID conviteId, UUID remetenteId) {
        servico.cancelar(new ConviteId(conviteId), remetenteId);
    }
}
