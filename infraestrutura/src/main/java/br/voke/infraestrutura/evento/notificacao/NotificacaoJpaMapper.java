package br.voke.infraestrutura.evento.notificacao;

import br.voke.dominio.evento.notificacao.Notificacao;
import br.voke.dominio.evento.notificacao.NotificacaoId;
import br.voke.infraestrutura.compartilhado.DominioReflection;

public final class NotificacaoJpaMapper {

    private NotificacaoJpaMapper() {
    }

    public static NotificacaoJpa paraJpa(Notificacao notificacao) {
        return new NotificacaoJpa(notificacao.getId().getValor(), notificacao.getEventoId(),
                notificacao.getConteudo(), notificacao.getDataEnvio(), notificacao.isEditada());
    }

    public static Notificacao paraDominio(NotificacaoJpa jpa) {
        Notificacao notificacao = new Notificacao(new NotificacaoId(jpa.getId()), jpa.getEventoId(), jpa.getConteudo());
        DominioReflection.definirCampo(notificacao, "dataEnvio", jpa.getDataEnvio());
        DominioReflection.definirCampo(notificacao, "editada", jpa.isEditada());
        return notificacao;
    }
}
