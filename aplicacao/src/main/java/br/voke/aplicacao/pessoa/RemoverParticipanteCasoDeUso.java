package br.voke.aplicacao.pessoa;

import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.dominio.pessoa.participante.ParticipanteServico;

import java.util.Objects;
import java.util.UUID;

public class RemoverParticipanteCasoDeUso {

    private final ParticipanteServico servico;

    public RemoverParticipanteCasoDeUso(ParticipanteServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID participanteId) {
        servico.remover(new ParticipanteId(participanteId));
    }
}
