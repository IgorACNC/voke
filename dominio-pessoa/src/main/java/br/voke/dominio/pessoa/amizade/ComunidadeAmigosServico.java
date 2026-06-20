package br.voke.dominio.pessoa.amizade;

import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.Objects;
import java.util.UUID;

public class ComunidadeAmigosServico implements ComunidadeAmigosOperacoes {

    private final ComunidadeAmigosRepositorio repositorio;

    public ComunidadeAmigosServico(ComunidadeAmigosRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositorio e obrigatorio");
        this.repositorio = repositorio;
    }

    public ComunidadeAmigos criar(NomeCompleto nome, ParticipanteId criadorId) {
        ComunidadeAmigos comunidade = new ComunidadeAmigos(
                ComunidadeAmigosId.novo(), nome, criadorId
        );
        repositorio.salvar(comunidade);
        return comunidade;
    }

    public void adicionarMembro(ComunidadeAmigosId comunidadeId, ParticipanteId membroId) {
        ComunidadeAmigos comunidade = repositorio.buscarPorId(comunidadeId)
                .orElseThrow(() -> new IllegalArgumentException("Comunidade nao encontrada"));
        comunidade.adicionarMembro(membroId);
        repositorio.salvar(comunidade);
    }

    public void compartilharEvento(ComunidadeAmigosId comunidadeId, UUID eventoId) {
        ComunidadeAmigos comunidade = repositorio.buscarPorId(comunidadeId)
                .orElseThrow(() -> new IllegalArgumentException("Comunidade nao encontrada"));
        comunidade.compartilharEvento(eventoId);
        repositorio.salvar(comunidade);
    }
}
