package br.voke.aplicacao.convite;

import br.voke.dominio.inscricao.convite.Convite;
import br.voke.dominio.inscricao.convite.ConviteServico;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListarConvitesRecebidosCasoDeUso {

    private final ConviteServico servico;

    public ListarConvitesRecebidosCasoDeUso(ConviteServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public List<Convite> executar(UUID destinatarioId) {
        return servico.listarRecebidos(destinatarioId);
    }
}
