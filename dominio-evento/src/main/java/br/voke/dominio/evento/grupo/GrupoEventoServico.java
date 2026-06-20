package br.voke.dominio.evento.grupo;

import java.util.Objects;
import java.util.UUID;

/**
 * Componente Concreto (ConcreteComponent) no padrão Decorator.
 * Responsável exclusivamente pela manipulação direta da entidade e persistência.
 * As regras de validação (idade, inscrição, privilégio) são tratadas pelos
 * decorators da cadeia.
 */
public class GrupoEventoServico implements GrupoEventoServicoInterface {

    private final GrupoEventoRepositorio repositorio;

    public GrupoEventoServico(GrupoEventoRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
    }

    @Override
    public GrupoEvento criar(String nome, String regras, UUID eventoId, UUID organizadorId) {
        GrupoEvento grupo = new GrupoEvento(GrupoEventoId.novo(), nome, regras, eventoId, organizadorId);
        repositorio.salvar(grupo);
        return grupo;
    }

    @Override
    public void adicionarMembro(GrupoEventoId grupoId, UUID participanteId,
                                 boolean possuiInscricao, int idadeParticipante) {
        GrupoEvento grupo = repositorio.buscarPorId(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));
        grupo.adicionarMembro(participanteId);
        repositorio.salvar(grupo);
    }

    @Override
    public void editarRegras(GrupoEventoId grupoId, String novasRegras, UUID solicitanteId) {
        GrupoEvento grupo = repositorio.buscarPorId(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));
        grupo.atualizarRegras(novasRegras);
        repositorio.salvar(grupo);
    }

    @Override
    public void remover(GrupoEventoId grupoId, UUID solicitanteId) {
        repositorio.buscarPorId(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));
        repositorio.remover(grupoId);
    }

    /**
     * RN3 - gatilho de sistema chamado pelo scheduler de eventos expirados ou pelo
     * fluxo de cancelamento do evento. Não passa pela cadeia de decorators porque
     * é uma ação automática (sem solicitante humano). Idempotente: se o grupo não
     * existir, retorna em silêncio.
     */
    public void removerPorEvento(UUID eventoId) {
        Objects.requireNonNull(eventoId, "EventoId é obrigatório");
        repositorio.buscarPorEventoId(eventoId)
                .ifPresent(g -> repositorio.remover(g.getId()));
    }
}
