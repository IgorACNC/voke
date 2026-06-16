package br.voke.infraestrutura.evento.subgrupo;

import br.voke.dominio.evento.subgrupo.Subgrupo;
import br.voke.dominio.evento.subgrupo.SubgrupoId;
import br.voke.infraestrutura.compartilhado.DominioReflection;

import java.util.HashSet;

public final class SubgrupoJpaMapper {

    private SubgrupoJpaMapper() {
    }

    public static SubgrupoJpa paraJpa(Subgrupo subgrupo) {
        return new SubgrupoJpa(
                subgrupo.getId().getValor(),
                subgrupo.getNome(),
                subgrupo.getDescricao(),
                subgrupo.getRegras(),
                subgrupo.getGrupoEventoId(),
                subgrupo.getCategoria(),
                subgrupo.getTipo(),
                subgrupo.getLimiteMembros(),
                subgrupo.getModeradorId(),
                subgrupo.getMembrosIds());
    }

    public static Subgrupo paraDominio(SubgrupoJpa jpa) {
        Subgrupo subgrupo = new Subgrupo(
                new SubgrupoId(jpa.getId()),
                jpa.getNome(),
                jpa.getDescricao(),
                jpa.getRegras(),
                jpa.getGrupoEventoId(),
                jpa.getCategoria(),
                jpa.getTipo(),
                jpa.getLimiteMembros());
        // Reconstrói membros via reflection para pular a validação de lotação
        DominioReflection.definirCampo(subgrupo, "membrosIds", new HashSet<>(jpa.getMembrosIds()));
        if (jpa.getModeradorId() != null) {
            DominioReflection.definirCampo(subgrupo, "moderadorId", jpa.getModeradorId());
        }
        return subgrupo;
    }
}
