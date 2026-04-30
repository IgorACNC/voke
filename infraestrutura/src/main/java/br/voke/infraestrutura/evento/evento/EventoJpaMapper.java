package br.voke.infraestrutura.evento.evento;

import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.Lote;
import br.voke.dominio.evento.evento.StatusEvento;
import br.voke.infraestrutura.compartilhado.DominioReflection;

public final class EventoJpaMapper {

    private EventoJpaMapper() {
    }

    public static EventoJpa paraJpa(Evento evento) {
        return new EventoJpa(
                evento.getId().getValor(),
                evento.getNome(),
                evento.getDescricao(),
                evento.getLocal(),
                evento.getDataHoraInicio(),
                evento.getDataHoraFim(),
                evento.getCapacidadeMaxima(),
                evento.getOrganizadorId(),
                evento.getIdadeMinima(),
                evento.getStatus(),
                paraJpa(evento.getLoteAtual()));
    }

    public static Evento paraDominio(EventoJpa jpa) {
        Evento evento = new Evento(
                new EventoId(jpa.getId()),
                jpa.getNome(),
                jpa.getDescricao(),
                jpa.getLocal(),
                jpa.getDataHoraInicio(),
                jpa.getDataHoraFim(),
                jpa.getCapacidadeMaxima(),
                jpa.getOrganizadorId(),
                paraDominio(jpa.getLoteAtual()),
                jpa.getIdadeMinima());
        if (jpa.getStatus() != StatusEvento.ATIVO) {
            DominioReflection.definirCampo(evento, "status", jpa.getStatus());
        }
        return evento;
    }

    private static LoteJpa paraJpa(Lote lote) {
        if (lote == null) return null;
        return new LoteJpa(lote.getNumero(), lote.getPreco(), lote.getQuantidadeTotal(),
                lote.getQuantidadeVendida(), lote.isAtivo());
    }

    private static Lote paraDominio(LoteJpa jpa) {
        if (jpa == null) return null;
        Lote lote = new Lote(jpa.getNumero(), jpa.getPreco(), jpa.getQuantidadeTotal());
        DominioReflection.definirCampo(lote, "quantidadeVendida", jpa.getQuantidadeVendida());
        DominioReflection.definirCampo(lote, "ativo", jpa.isAtivo());
        return lote;
    }
}
