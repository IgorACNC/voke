package br.voke.dominio.evento.grupo;

import br.voke.dominio.evento.excecao.AcessoGrupoNegadoException;
import br.voke.dominio.evento.excecao.MenorDeIdadeGrupoException;

import java.util.Objects;
import java.util.UUID;

public class GrupoEventoServico {

    private final GrupoEventoRepositorio repositorio;

    public GrupoEventoServico(GrupoEventoRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
    }

    public GrupoEvento criar(String nome, String regras, UUID eventoId, UUID organizadorId) {
        GrupoEvento grupo = new GrupoEvento(GrupoEventoId.novo(), nome, regras, eventoId, organizadorId);
        repositorio.salvar(grupo);
        return grupo;
    }

    public void adicionarMembro(GrupoEventoId grupoId, UUID participanteId,
                                 boolean possuiInscricao, int idadeParticipante) {
        GrupoEvento grupo = repositorio.buscarPorId(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));
        if (idadeParticipante < 18) {
            throw new MenorDeIdadeGrupoException();
        }
        if (!possuiInscricao) {
            throw new AcessoGrupoNegadoException("Inscrição necessária para acessar este grupo");
        }
        grupo.adicionarMembro(participanteId);
        repositorio.salvar(grupo);
    }

    public void editarRegras(GrupoEventoId grupoId, String novasRegras) {
        GrupoEvento grupo = repositorio.buscarPorId(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));
        grupo.atualizarRegras(novasRegras);
        repositorio.salvar(grupo);
    }

    public void remover(GrupoEventoId grupoId) {
        repositorio.buscarPorId(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));
        repositorio.remover(grupoId);
    }
}
