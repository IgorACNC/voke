package br.voke.infraestrutura.evento.grupo;

import br.voke.dominio.evento.grupo.GrupoEvento;
import br.voke.dominio.evento.grupo.GrupoEventoId;

public final class GrupoEventoJpaMapper {

    private GrupoEventoJpaMapper() {
    }

    public static GrupoEventoJpa paraJpa(GrupoEvento grupo) {
        return new GrupoEventoJpa(grupo.getId().getValor(), grupo.getNome(), grupo.getRegras(),
                grupo.getEventoId(), grupo.getOrganizadorId(), grupo.getMembrosIds());
    }

    public static GrupoEvento paraDominio(GrupoEventoJpa jpa) {
        GrupoEvento grupo = new GrupoEvento(new GrupoEventoId(jpa.getId()), jpa.getNome(), jpa.getRegras(),
                jpa.getEventoId(), jpa.getOrganizadorId());
        jpa.getMembrosIds().forEach(grupo::adicionarMembro);
        return grupo;
    }
}
