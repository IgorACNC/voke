package br.voke.infraestrutura.pessoa.amizade;

import br.voke.dominio.pessoa.amizade.Amizade;
import br.voke.dominio.pessoa.amizade.AmizadeId;
import br.voke.dominio.pessoa.amizade.StatusAmizade;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.infraestrutura.compartilhado.DominioReflection;

public final class AmizadeJpaMapper {

    private AmizadeJpaMapper() {
    }

    public static AmizadeJpa paraJpa(Amizade amizade) {
        return new AmizadeJpa(
                amizade.getId().getValor(),
                amizade.getSolicitanteId().getValor(),
                amizade.getReceptorId().getValor(),
                amizade.getStatus());
    }

    public static Amizade paraDominio(AmizadeJpa jpa) {
        Amizade amizade = new Amizade(
                new AmizadeId(jpa.getId()),
                new ParticipanteId(jpa.getSolicitanteId()),
                new ParticipanteId(jpa.getReceptorId()));
        if (jpa.getStatus() != StatusAmizade.PENDENTE) {
            DominioReflection.definirCampo(amizade, "status", jpa.getStatus());
        }
        return amizade;
    }
}
