package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.sugestao.Sugestao;
import br.voke.dominio.fidelidade.sugestao.SugestaoServico;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GerarSugestoesSemanaisCasoDeUso {

    private final SugestaoServico servico;

    public GerarSugestoesSemanaisCasoDeUso(SugestaoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public List<Sugestao> executar(UUID participanteId, List<UUID> eventosSugeridos) {
        return servico.gerarSugestoesSemanais(participanteId, eventosSugeridos);
    }
}
