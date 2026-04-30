package br.voke.dominio.evento.avaliacao;

import br.voke.dominio.evento.excecao.AvaliacaoDuplicadaException;
import br.voke.dominio.evento.excecao.EventoNaoFinalizadoException;
import br.voke.dominio.evento.excecao.InscricaoNaoConfirmadaException;

import java.util.Objects;
import java.util.UUID;

public class AvaliacaoServico {

    private final AvaliacaoRepositorio repositorio;

    public AvaliacaoServico(AvaliacaoRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
    }

    public Avaliacao avaliar(UUID participanteId, UUID eventoId, int nota, String comentario,
                             boolean eventoFinalizado, boolean inscricaoConfirmada) {
        if (!eventoFinalizado) {
            throw new EventoNaoFinalizadoException();
        }
        if (!inscricaoConfirmada) {
            throw new InscricaoNaoConfirmadaException();
        }
        if (repositorio.existePorParticipanteEEvento(participanteId, eventoId)) {
            throw new AvaliacaoDuplicadaException();
        }
        Avaliacao avaliacao = new Avaliacao(AvaliacaoId.novo(), participanteId, eventoId, nota, comentario);
        repositorio.salvar(avaliacao);
        return avaliacao;
    }

    public void editar(AvaliacaoId id, int novaNota, String novoComentario) {
        Avaliacao avaliacao = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));
        avaliacao.editar(novaNota, novoComentario);
        repositorio.salvar(avaliacao);
    }

    public void remover(AvaliacaoId id) {
        repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));
        repositorio.remover(id);
    }
}
