package br.voke.dominio.fidelidade.sugestao;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class SugestaoServico {

    private static final int LIMITE_SEMANAL_POR_PARTICIPANTE = 4;

    private final SugestaoRepositorio repositorio;
    private final PreferenciaParticipanteRepositorio preferenciaRepositorio;
    private final MotorSugestoes motor;
    private final EventoConsultaGateway eventoGateway;
    private final List<SugestaoObserver> observers = new ArrayList<>();

    public SugestaoServico(SugestaoRepositorio repositorio,
                           PreferenciaParticipanteRepositorio preferenciaRepositorio,
                           MotorSugestoes motor,
                           EventoConsultaGateway eventoGateway) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        Objects.requireNonNull(preferenciaRepositorio, "Repositório de preferências é obrigatório");
        Objects.requireNonNull(motor, "Motor de sugestões é obrigatório");
        Objects.requireNonNull(eventoGateway, "EventoConsultaGateway é obrigatório");
        this.repositorio = repositorio;
        this.preferenciaRepositorio = preferenciaRepositorio;
        this.motor = motor;
        this.eventoGateway = eventoGateway;
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

    public List<Sugestao> gerarSugestoesSemanais(UUID participanteId) {
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        PreferenciaParticipante preferencia = preferenciaRepositorio.buscarPorParticipanteId(participanteId)
                .orElseThrow(() -> new IllegalStateException(
                        "Participante não configurou preferências de sugestões"));

        int jaEnviadas = repositorio.contarSugestoesSemanalPorParticipante(participanteId);
        int podeEnviar = Math.max(0, LIMITE_SEMANAL_POR_PARTICIPANTE - jaEnviadas);
        if (podeEnviar == 0) return List.of();

        List<UUID> eventosRecomendados = motor.recomendar(preferencia, podeEnviar);
        return eventosRecomendados.stream()
                .map(eventoId -> {
                    Sugestao s = new Sugestao(SugestaoId.novo(), participanteId, eventoId,
                            "Sugestão semanal personalizada");
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
            registrarFeedbackNegativo(sugestao);
        }
        repositorio.salvar(sugestao);
        notificarObservers(sugestao, statusAnterior);
    }

    private void registrarFeedbackNegativo(Sugestao sugestao) {
        Optional<PreferenciaParticipante> optPref =
                preferenciaRepositorio.buscarPorParticipanteId(sugestao.getParticipanteId());
        if (optPref.isEmpty()) return;
        var categoriasEvento = eventoGateway.buscarCategoriasDoEvento(sugestao.getEventoId());
        if (categoriasEvento.isEmpty()) return;
        PreferenciaParticipante pref = optPref.get();
        pref.registrarFeedbackNegativo(categoriasEvento);
        preferenciaRepositorio.salvar(pref);
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

    public int expirarSugestoesAntigas(int diasParaExpirar) {
        List<Sugestao> antigas = repositorio.buscarPendentesCriadasAntesDe(diasParaExpirar);
        antigas.forEach(s -> {
            StatusSugestao anterior = s.getStatus();
            s.expirar();
            repositorio.salvar(s);
            notificarObservers(s, anterior);
        });
        return antigas.size();
    }

    public void remover(SugestaoId id) {
        repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Sugestão não encontrada"));
        repositorio.remover(id);
    }

    public List<Sugestao> listarPorParticipante(UUID participanteId) {
        return repositorio.buscarPorParticipanteId(participanteId);
    }

    public boolean participanteTemPreferencias(UUID participanteId) {
        return preferenciaRepositorio.buscarPorParticipanteId(participanteId)
                .map(p -> !p.getCategoriaIds().isEmpty())
                .orElse(false);
    }

    public java.util.Set<UUID> buscarCategoriasDoParticipante(UUID participanteId) {
        return preferenciaRepositorio.buscarPorParticipanteId(participanteId)
                .map(PreferenciaParticipante::getCategoriaIds)
                .orElse(java.util.Set.of());
    }

    public PreferenciaParticipante configurarPreferencias(UUID participanteId, java.util.Set<UUID> categoriaIds) {
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        Objects.requireNonNull(categoriaIds, "Categorias são obrigatórias");
        PreferenciaParticipante pref = preferenciaRepositorio.buscarPorParticipanteId(participanteId)
                .orElseGet(() -> new PreferenciaParticipante(
                        PreferenciaParticipanteId.novo(), participanteId));
        pref.definirCategorias(categoriaIds);
        preferenciaRepositorio.salvar(pref);
        return pref;
    }
}
