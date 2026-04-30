package br.voke.infraestrutura.fidelidade.sugestao;

import br.voke.dominio.fidelidade.sugestao.StatusSugestao;
import br.voke.dominio.fidelidade.sugestao.Sugestao;
import br.voke.dominio.fidelidade.sugestao.SugestaoId;
import br.voke.infraestrutura.compartilhado.DominioReflection;

import java.time.LocalDateTime;

public final class SugestaoJpaMapper {

    private SugestaoJpaMapper() {
    }

    public static SugestaoJpa paraJpa(Sugestao sugestao) {
        return new SugestaoJpa(sugestao.getId().getValor(), sugestao.getParticipanteId(),
                sugestao.getEventoId(), sugestao.getStatus(), LocalDateTime.now());
    }

    public static Sugestao paraDominio(SugestaoJpa jpa) {
        Sugestao sugestao = new Sugestao(new SugestaoId(jpa.getId()), jpa.getParticipanteId(), jpa.getEventoId());
        if (jpa.getStatus() != StatusSugestao.PENDENTE) {
            DominioReflection.definirCampo(sugestao, "status", jpa.getStatus());
        }
        return sugestao;
    }
}
