package br.voke.dominio.pessoa.chat;

import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.List;

public interface MensagemPrivadaRepositorio {
    void salvar(MensagemPrivada mensagem);
    List<MensagemPrivada> listarConversa(ParticipanteId participanteA, ParticipanteId participanteB);
}
