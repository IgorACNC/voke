package br.voke.infraestrutura.pessoa.chat;

import br.voke.dominio.pessoa.chat.MensagemPrivada;
import br.voke.dominio.pessoa.chat.MensagemPrivadaId;
import br.voke.dominio.pessoa.participante.ParticipanteId;

public final class MensagemPrivadaJpaMapper {

    private MensagemPrivadaJpaMapper() {
    }

    public static MensagemPrivadaJpa paraJpa(MensagemPrivada mensagem) {
        return new MensagemPrivadaJpa(
                mensagem.getId().getValor(),
                mensagem.getRemetenteId().getValor(),
                mensagem.getDestinatarioId().getValor(),
                mensagem.getConteudo(),
                mensagem.getEnviadaEm());
    }

    public static MensagemPrivada paraDominio(MensagemPrivadaJpa jpa) {
        return new MensagemPrivada(
                new MensagemPrivadaId(jpa.getId()),
                new ParticipanteId(jpa.getRemetenteId()),
                new ParticipanteId(jpa.getDestinatarioId()),
                jpa.getConteudo(),
                jpa.getEnviadaEm());
    }
}
