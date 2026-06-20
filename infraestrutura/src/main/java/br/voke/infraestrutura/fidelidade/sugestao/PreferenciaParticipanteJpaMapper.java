package br.voke.infraestrutura.fidelidade.sugestao;

import br.voke.dominio.fidelidade.sugestao.PreferenciaParticipante;
import br.voke.dominio.fidelidade.sugestao.PreferenciaParticipanteId;

public final class PreferenciaParticipanteJpaMapper {

    private PreferenciaParticipanteJpaMapper() {
    }

    public static PreferenciaParticipanteJpa paraJpa(PreferenciaParticipante preferencia) {
        return new PreferenciaParticipanteJpa(
                preferencia.getId().getValor(),
                preferencia.getParticipanteId(),
                preferencia.getCategoriaIds(),
                preferencia.getPesosNegativos());
    }

    public static PreferenciaParticipante paraDominio(PreferenciaParticipanteJpa jpa) {
        return new PreferenciaParticipante(
                new PreferenciaParticipanteId(jpa.getId()),
                jpa.getParticipanteId(),
                jpa.getCategoriaIds(),
                jpa.getPesosNegativos());
    }
}
