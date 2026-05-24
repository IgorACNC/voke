package br.voke.dominio.fidelidade.pontos;

import java.util.Objects;
import java.util.UUID;

public class ContaPontosServico {

    private final ContaPontosRepositorio repositorio;
    private final ExpiracaoPontosNotificador notificador;

    public ContaPontosServico(ContaPontosRepositorio repositorio) {
        this(repositorio, (participanteId, pontosExpirados) -> { });
    }

    public ContaPontosServico(ContaPontosRepositorio repositorio, ExpiracaoPontosNotificador notificador) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
        this.notificador = Objects.requireNonNull(notificador, "Notificador e obrigatorio");
    }

    public ContaPontos obterOuCriar(UUID participanteId) {
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        return repositorio.buscarPorParticipanteId(participanteId)
                .orElseGet(() -> {
                    ContaPontos nova = new ContaPontos(ContaPontosId.novo(), participanteId);
                    repositorio.salvar(nova);
                    return nova;
                });
    }

    public void creditarPorPresenca(UUID participanteId, int pontosBase,
                                    boolean eventoEncerrado, boolean checkInRealizado) {
        creditarPorPresenca(participanteId, pontosBase, eventoEncerrado, checkInRealizado,
                new GanhoPontosRegular());
    }

    public void creditarPorPresenca(UUID participanteId, int pontosBase,
                                    boolean eventoEncerrado, boolean checkInRealizado,
                                    EstrategiaGanhoPontos estrategia) {
        Objects.requireNonNull(estrategia, "Estratégia de ganho de pontos é obrigatória");
        if (!eventoEncerrado || !checkInRealizado) {
            throw new IllegalStateException(
                    "Pontos só podem ser creditados após check-in em evento encerrado");
        }
        int pontosCalculados = estrategia.calcular(pontosBase);
        ContaPontos conta = obterOuCriar(participanteId);
        conta.creditarPorPresenca(pontosCalculados);
        repositorio.salvar(conta);
    }

    public void debitar(UUID participanteId, int pontos) {
        ContaPontos conta = repositorio.buscarPorParticipanteId(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Conta de pontos não encontrada"));
        conta.debitar(pontos);
        repositorio.salvar(conta);
    }

    public void expirarPontos(UUID participanteId, int pontosExpirados) {
        ContaPontos conta = repositorio.buscarPorParticipanteId(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Conta de pontos não encontrada"));
        conta.expirarPontos(pontosExpirados);
        repositorio.salvar(conta);
        if (pontosExpirados > 0) {
            notificador.notificarExpiracao(participanteId, pontosExpirados);
        }
    }

    public int consultarSaldo(UUID participanteId) {
        return repositorio.buscarPorParticipanteId(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Conta de pontos não encontrada"))
                .getSaldo();
    }
}