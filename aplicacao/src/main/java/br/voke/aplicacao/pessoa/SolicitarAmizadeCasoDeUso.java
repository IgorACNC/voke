package br.voke.aplicacao.pessoa;

import br.voke.dominio.pessoa.amizade.Amizade;
import br.voke.dominio.pessoa.amizade.AmizadeServico;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;

import java.util.Objects;
import java.util.UUID;

public class SolicitarAmizadeCasoDeUso {

    private final AmizadeServico servico;
    private final ParticipanteRepositorio participanteRepositorio;

    public SolicitarAmizadeCasoDeUso(AmizadeServico servico,
                                     ParticipanteRepositorio participanteRepositorio) {
        Objects.requireNonNull(servico);
        Objects.requireNonNull(participanteRepositorio);
        this.servico = servico;
        this.participanteRepositorio = participanteRepositorio;
    }

    public Amizade executar(UUID solicitanteId, UUID receptorId) {
        Participante solicitante = participanteRepositorio.buscarPorId(new ParticipanteId(solicitanteId))
                .orElseThrow(() -> new IllegalArgumentException("Participante solicitante não encontrado"));
        return servico.enviarSolicitacao(solicitante, new ParticipanteId(receptorId));
    }
}
