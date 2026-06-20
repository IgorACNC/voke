package br.voke.dominio.pessoa.amizade;

import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.pessoa.excecao.VinculoDeAmizadeNecessarioException;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.Objects;
import java.util.UUID;

public class ComunidadeAmigosProtecaoProxy implements ComunidadeAmigosOperacoes {

    private final ComunidadeAmigosOperacoes servico;
    private final AmizadeServico amizadeServico;

    public ComunidadeAmigosProtecaoProxy(ComunidadeAmigosOperacoes servico,
                                         AmizadeServico amizadeServico) {
        Objects.requireNonNull(servico, "Servico de comunidade e obrigatorio");
        Objects.requireNonNull(amizadeServico, "Servico de amizade e obrigatorio");
        this.servico = servico;
        this.amizadeServico = amizadeServico;
    }

    public ComunidadeAmigos criar(NomeCompleto nome, ParticipanteId criadorId) {
        if (!amizadeServico.possuiAmizadeAtiva(criadorId)) {
            throw new VinculoDeAmizadeNecessarioException();
        }
        return servico.criar(nome, criadorId);
    }

    public void adicionarMembro(ComunidadeAmigosId comunidadeId, ParticipanteId membroId) {
        servico.adicionarMembro(comunidadeId, membroId);
    }

    public void compartilharEvento(ComunidadeAmigosId comunidadeId, UUID eventoId) {
        servico.compartilharEvento(comunidadeId, eventoId);
    }
}
