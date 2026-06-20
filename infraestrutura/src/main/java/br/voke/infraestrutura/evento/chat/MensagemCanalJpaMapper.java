package br.voke.infraestrutura.evento.chat;

import br.voke.dominio.evento.chat.MensagemCanal;
import br.voke.dominio.evento.chat.MensagemCanalId;

public final class MensagemCanalJpaMapper {

    private MensagemCanalJpaMapper() {
    }

    public static MensagemCanalJpa paraJpa(MensagemCanal m) {
        return new MensagemCanalJpa(
                m.getId().getValor(), m.getCanalTipo(), m.getCanalId(),
                m.getRemetenteId(), m.getConteudo(), m.getEnviadaEm());
    }

    public static MensagemCanal paraDominio(MensagemCanalJpa jpa) {
        return new MensagemCanal(
                new MensagemCanalId(jpa.getId()), jpa.getCanalTipo(), jpa.getCanalId(),
                jpa.getRemetenteId(), jpa.getConteudo(), jpa.getEnviadaEm());
    }
}
