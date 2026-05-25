package br.voke.dominio.fidelidade.sugestao;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SugestaoServico {

    private static final int LIMITE_SEMANAL_POR_PARTICIPANTE = 4;

    private final SugestaoRepositorio repositorio;
    private final List<SugestaoObserver> observers = new ArrayList<>();

    public SugestaoServico(SugestaoRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
    }

    public void registrarObserver(SugestaoObserver observer) {
        Objects.requireNonNull(observer, "Observer é obrigatório");
        observers.add(observer);
    }

    private void notificarObservers(Sugestao sugestao, StatusSugestao statusAnterior) {
        observers.forEach(o -> o.aoMudarStatus(sugestao, statusAnterior));
    }

    public Sugestao cadastrar(UUID participanteId, UUID eventoId, String descricao) {
        Sugestao sugestao = new Sugestao(SugestaoId.novo(), participanteId, eventoId, descricao);
        repositorio.salvar(sugestao);
        return sugestao;
    }

    public List<Sugestao> gerarSugestoesSemanais(UUID participanteId, List<UUID> eventosSugeridos) {
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        Objects.requireNonNull(eventosSugeridos, "Lista de eventos é obrigatória");
        int jaEnviadas = repositorio.contarSugestoesSemanalPorParticipante(participanteId);
        int podeEnviar = Math.max(0, LIMITE_SEMANAL_POR_PARTICIPANTE - jaEnviadas);
        return eventosSugeridos.stream()
                .limit(podeEnviar)
                .map(eventoId -> {
                    Sugestao s = new Sugestao(SugestaoId.novo(), participanteId, eventoId,
                            "Sugestão semanal");
                    repositorio.salvar(s);
                    return s;
                })
                .toList();
    }

    public void avaliar(SugestaoId id, boolean aprovada) {
        Sugestao sugestao = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Sugestão não encontrada"));
        StatusSugestao statusAnterior = sugestao.getStatus();
        if (aprovada) {
            sugestao.aprovar();
        } else {
            sugestao.rejeitar();
        }
        repositorio.salvar(sugestao);
        notificarObservers(sugestao, statusAnterior);
    }

    public void editar(SugestaoId id, String novaDescricao) {
        Sugestao sugestao = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Sugestão não encontrada"));
        sugestao.editar(novaDescricao);
        repositorio.salvar(sugestao);
    }

    public void expirar(SugestaoId id) {
        Sugestao sugestao = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Sugestão não encontrada"));
        StatusSugestao statusAnterior = sugestao.getStatus();
        sugestao.expirar();
        repositorio.salvar(sugestao);
        notificarObservers(sugestao, statusAnterior);
    }

    public void remover(SugestaoId id) {
        repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Sugestão não encontrada"));
        repositorio.remover(id);
    }

    public List<Sugestao> listarPorParticipante(UUID participanteId) {
        return repositorio.buscarPorParticipanteId(participanteId);
    }
}
