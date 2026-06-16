package br.voke.infraestrutura.evento.notificacao;

import br.voke.dominio.evento.notificacao.Notificacao;
import br.voke.dominio.evento.notificacao.NotificacaoId;
import br.voke.infraestrutura.compartilhado.DominioReflection;

public final class NotificacaoJpaMapper {

    private NotificacaoJpaMapper() {
    }

    public static NotificacaoJpa paraJpa(Notificacao notificacao) {
        return new NotificacaoJpa(
                notificacao.getId().getValor(),
                notificacao.getEventoId(),
                notificacao.getConteudo(),
                notificacao.getDataEnvio(),
                notificacao.isEditada(),
                notificacao.getStatus(),
                notificacao.getDataAgendamento(),
                notificacao.getContadorEdicoes(),
                notificacao.getCriterioSegmentacao(),
                notificacao.getDestinatariosIds());
    }

    public static Notificacao paraDominio(NotificacaoJpa jpa) {
        Notificacao notificacao = new Notificacao(
                new NotificacaoId(jpa.getId()),
                jpa.getEventoId(),
                jpa.getConteudo(),
                jpa.getDestinatariosIds());
        DominioReflection.definirCampo(notificacao, "dataEnvio", jpa.getDataEnvio());
        DominioReflection.definirCampo(notificacao, "editada", jpa.isEditada());
        DominioReflection.definirCampo(notificacao, "status", jpa.getStatus());
        DominioReflection.definirCampo(notificacao, "dataAgendamento", jpa.getDataAgendamento());
        DominioReflection.definirCampo(notificacao, "contadorEdicoes", jpa.getContadorEdicoes());
        DominioReflection.definirCampo(notificacao, "criterioSegmentacao", jpa.getCriterioSegmentacao());
        return notificacao;
    }
}
