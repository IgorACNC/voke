package br.voke.dominio.pessoa.chat;

import br.voke.dominio.pessoa.amizade.AmizadeRepositorio;
import br.voke.dominio.pessoa.amizade.StatusAmizade;
import br.voke.dominio.pessoa.excecao.ParticipantesNaoSaoAmigosException;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ChatPrivadoServico {

    private final MensagemPrivadaRepositorio mensagemRepositorio;
    private final AmizadeRepositorio amizadeRepositorio;

    public ChatPrivadoServico(MensagemPrivadaRepositorio mensagemRepositorio,
                              AmizadeRepositorio amizadeRepositorio) {
        Objects.requireNonNull(mensagemRepositorio, "Repositorio de mensagens e obrigatorio");
        Objects.requireNonNull(amizadeRepositorio, "Repositorio de amizades e obrigatorio");
        this.mensagemRepositorio = mensagemRepositorio;
        this.amizadeRepositorio = amizadeRepositorio;
    }

    public MensagemPrivada enviar(ParticipanteId remetenteId, ParticipanteId destinatarioId, String conteudo) {
        validarAmizadeAtiva(remetenteId, destinatarioId);
        MensagemPrivada mensagem = new MensagemPrivada(
                MensagemPrivadaId.novo(), remetenteId, destinatarioId, conteudo, LocalDateTime.now());
        mensagemRepositorio.salvar(mensagem);
        return mensagem;
    }

    public List<MensagemPrivada> listarConversa(ParticipanteId participanteA, ParticipanteId participanteB) {
        validarAmizadeAtiva(participanteA, participanteB);
        return mensagemRepositorio.listarConversa(participanteA, participanteB);
    }

    private void validarAmizadeAtiva(ParticipanteId participanteA, ParticipanteId participanteB) {
        boolean saoAmigos = amizadeRepositorio.buscarPorParticipante(participanteA).stream()
                .anyMatch(a -> a.getStatus() == StatusAmizade.ATIVA
                        && ((a.getSolicitanteId().equals(participanteA) && a.getReceptorId().equals(participanteB))
                        || (a.getSolicitanteId().equals(participanteB) && a.getReceptorId().equals(participanteA))));
        if (!saoAmigos) {
            throw new ParticipantesNaoSaoAmigosException();
        }
    }
}
