package br.voke.infraestrutura.inscricao.convite;

import br.voke.dominio.inscricao.convite.Convite;
import br.voke.dominio.inscricao.convite.ConviteId;
import br.voke.infraestrutura.compartilhado.DominioReflection;

public final class ConviteJpaMapper {

    private ConviteJpaMapper() {}

    public static ConviteJpa paraJpa(Convite c) {
        return new ConviteJpa(c.getId().getValor(), c.getRemetenteId(), c.getDestinatarioId(),
                c.getEventoId(), c.getStatus(), c.getCriadoEm(), c.getExpiraEm());
    }

    public static Convite paraDominio(ConviteJpa jpa) {
        Convite convite = new Convite(new ConviteId(jpa.getId()), jpa.getRemetenteId(),
                jpa.getDestinatarioId(), jpa.getEventoId());
        DominioReflection.definirCampo(convite, "status", jpa.getStatus());
        DominioReflection.definirCampo(convite, "criadoEm", jpa.getCriadoEm());
        DominioReflection.definirCampo(convite, "expiraEm", jpa.getExpiraEm());
        return convite;
    }
}
