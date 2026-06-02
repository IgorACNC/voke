package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.sugestao.NotificarParticipanteObserver;
import br.voke.dominio.fidelidade.sugestao.SugestaoId;
import br.voke.dominio.fidelidade.sugestao.SugestaoServico;

import java.util.Objects;
import java.util.UUID;

public class AvaliarSugestaoCasoDeUso {

    private final SugestaoServico servico;

    public AvaliarSugestaoCasoDeUso(SugestaoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
        this.servico.registrarObserver(new NotificarParticipanteObserver());
    }

    public void executar(UUID sugestaoId, boolean aprovada) {
        servico.avaliar(new SugestaoId(sugestaoId), aprovada);
    }
}
