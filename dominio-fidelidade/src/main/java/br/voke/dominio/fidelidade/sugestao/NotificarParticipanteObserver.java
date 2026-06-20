package br.voke.dominio.fidelidade.sugestao;

public class NotificarParticipanteObserver implements SugestaoObserver {

    @Override
    public void aoMudarStatus(Sugestao sugestao, StatusSugestao statusAnterior) {
        System.out.printf(
            "[Notificação] Participante %s: sua sugestão para o evento %s mudou de %s para %s%n",
            sugestao.getParticipanteId(),
            sugestao.getEventoId(),
            statusAnterior,
            sugestao.getStatus()
        );
    }
}
