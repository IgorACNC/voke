package br.voke.aplicacao.pessoa;

import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.dominio.pessoa.participante.ParticipanteServico;

import java.util.Objects;
import java.util.UUID;

public class EditarParticipanteCasoDeUso {

    private final ParticipanteServico servico;

    public EditarParticipanteCasoDeUso(ParticipanteServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID participanteId, String novoNome, String novoEmail) {
        servico.atualizarDados(
                new ParticipanteId(participanteId),
                new NomeCompleto(novoNome),
                new Email(novoEmail)
        );
    }
}
