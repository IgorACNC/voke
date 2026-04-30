package br.voke.dominio.pessoa.amizade;

import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.pessoa.excecao.VinculoDeAmizadeNecessarioException;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.Objects;
import java.util.UUID;

public class ComunidadeAmigosServico {

    private final ComunidadeAmigosRepositorio repositorio;
    private final AmizadeServico amizadeServico;

    public ComunidadeAmigosServico(ComunidadeAmigosRepositorio repositorio,
                                    AmizadeServico amizadeServico) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        Objects.requireNonNull(amizadeServico, "Serviço de amizade é obrigatório");
        this.repositorio = repositorio;
        this.amizadeServico = amizadeServico;
    }

    public ComunidadeAmigos criar(NomeCompleto nome, ParticipanteId criadorId) {
        if (!amizadeServico.possuiAmizadeAtiva(criadorId)) {
            throw new VinculoDeAmizadeNecessarioException();
        }
        ComunidadeAmigos comunidade = new ComunidadeAmigos(
                ComunidadeAmigosId.novo(), nome, criadorId
        );
        repositorio.salvar(comunidade);
        return comunidade;
    }

    public void adicionarMembro(ComunidadeAmigosId comunidadeId, ParticipanteId membroId) {
        ComunidadeAmigos comunidade = repositorio.buscarPorId(comunidadeId)
                .orElseThrow(() -> new IllegalArgumentException("Comunidade não encontrada"));
        comunidade.adicionarMembro(membroId);
        repositorio.salvar(comunidade);
    }

    public void compartilharEvento(ComunidadeAmigosId comunidadeId, UUID eventoId) {
        ComunidadeAmigos comunidade = repositorio.buscarPorId(comunidadeId)
                .orElseThrow(() -> new IllegalArgumentException("Comunidade não encontrada"));
        comunidade.compartilharEvento(eventoId);
        repositorio.salvar(comunidade);
    }
}
