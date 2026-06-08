package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.sugestao.PreferenciaParticipante;
import br.voke.dominio.fidelidade.sugestao.SugestaoServico;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class ConfigurarPreferenciasCasoDeUso {

    private final SugestaoServico servico;

    public ConfigurarPreferenciasCasoDeUso(SugestaoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public PreferenciaParticipante executar(UUID participanteId, Set<String> categorias) {
        return servico.configurarPreferencias(participanteId, categorias);
    }
}
