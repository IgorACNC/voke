package br.voke.aplicacao.pessoa;

import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.pessoa.amizade.AmizadeServico;
import br.voke.dominio.pessoa.amizade.ComunidadeAmigos;
import br.voke.dominio.pessoa.amizade.ComunidadeAmigosOperacoes;
import br.voke.dominio.pessoa.amizade.ComunidadeAmigosProtecaoProxy;
import br.voke.dominio.pessoa.amizade.ComunidadeAmigosRepositorio;
import br.voke.dominio.pessoa.amizade.ComunidadeAmigosServico;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.Objects;
import java.util.UUID;

public class CriarComunidadeCasoDeUso {

    private final ComunidadeAmigosOperacoes servico;

    public CriarComunidadeCasoDeUso(ComunidadeAmigosRepositorio repositorio,
                                     AmizadeServico amizadeServico) {
        Objects.requireNonNull(repositorio);
        Objects.requireNonNull(amizadeServico);
        ComunidadeAmigosServico servicoReal = new ComunidadeAmigosServico(repositorio);
        this.servico = new ComunidadeAmigosProtecaoProxy(servicoReal, amizadeServico);
    }

    public ComunidadeAmigos executar(UUID criadorId, String nome) {
        return servico.criar(new NomeCompleto(nome), new ParticipanteId(criadorId));
    }
}
