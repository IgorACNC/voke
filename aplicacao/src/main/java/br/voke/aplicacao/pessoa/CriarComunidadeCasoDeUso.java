package br.voke.aplicacao.pessoa;

import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.pessoa.amizade.ComunidadeAmigos;
import br.voke.dominio.pessoa.amizade.ComunidadeAmigosOperacoes;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.Objects;
import java.util.UUID;

public class CriarComunidadeCasoDeUso {

    private final ComunidadeAmigosOperacoes servico;

    public CriarComunidadeCasoDeUso(ComunidadeAmigosOperacoes servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public ComunidadeAmigos executar(UUID criadorId, String nome) {
        return servico.criar(new NomeCompleto(nome), new ParticipanteId(criadorId));
    }
}
