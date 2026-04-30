package br.voke.dominio.pessoa.amizade;

import br.voke.dominio.pessoa.excecao.AcessoRestritoPorIdadeException;
import br.voke.dominio.pessoa.excecao.AmizadeJaExisteException;
import br.voke.dominio.pessoa.excecao.AmizadeNaoEncontradaException;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.Objects;

public class AmizadeServico {

    private static final int IDADE_MINIMA_AMIZADE = 16;

    private final AmizadeRepositorio repositorio;

    public AmizadeServico(AmizadeRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
    }

    public Amizade enviarSolicitacao(Participante solicitante, ParticipanteId receptorId) {
        if (solicitante.getIdade() < IDADE_MINIMA_AMIZADE) {
            throw new AcessoRestritoPorIdadeException();
        }
        if (repositorio.existeEntreParticipantes(solicitante.getId(), receptorId)) {
            throw new AmizadeJaExisteException();
        }
        Amizade amizade = new Amizade(AmizadeId.novo(), solicitante.getId(), receptorId);
        repositorio.salvar(amizade);
        return amizade;
    }

    public void aceitar(AmizadeId amizadeId) {
        Amizade amizade = repositorio.buscarPorId(amizadeId)
                .orElseThrow(AmizadeNaoEncontradaException::new);
        amizade.aceitar();
        repositorio.salvar(amizade);
    }

    public void recusar(AmizadeId amizadeId) {
        Amizade amizade = repositorio.buscarPorId(amizadeId)
                .orElseThrow(AmizadeNaoEncontradaException::new);
        amizade.recusar();
        repositorio.salvar(amizade);
    }

    public void desfazer(AmizadeId amizadeId) {
        Amizade amizade = repositorio.buscarPorId(amizadeId)
                .orElseThrow(AmizadeNaoEncontradaException::new);
        amizade.desfazer();
        repositorio.salvar(amizade);
    }

    public boolean possuiAmizadeAtiva(ParticipanteId participanteId) {
        return !repositorio.buscarAtivasPorParticipante(participanteId).isEmpty();
    }
}
