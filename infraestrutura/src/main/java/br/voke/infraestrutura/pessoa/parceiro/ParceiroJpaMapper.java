package br.voke.infraestrutura.pessoa.parceiro;

import br.voke.dominio.pessoa.organizador.OrganizadorId;
import br.voke.dominio.pessoa.parceiro.Parceiro;
import br.voke.dominio.pessoa.parceiro.ParceiroId;
import br.voke.dominio.pessoa.participante.ParticipanteId;

public final class ParceiroJpaMapper {

    private ParceiroJpaMapper() {
    }

    public static ParceiroJpa paraJpa(Parceiro parceiro) {
        return new ParceiroJpa(
                parceiro.getId().getValor(),
                parceiro.getParticipanteId().getValor(),
                parceiro.getOrganizadorId().getValor(),
                parceiro.getAtividades());
    }

    public static Parceiro paraDominio(ParceiroJpa jpa) {
        return new Parceiro(
                new ParceiroId(jpa.getId()),
                new ParticipanteId(jpa.getParticipanteId()),
                new OrganizadorId(jpa.getOrganizadorId()),
                jpa.getAtividades());
    }
}
