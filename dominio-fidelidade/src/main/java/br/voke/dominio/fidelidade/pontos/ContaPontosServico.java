package br.voke.dominio.fidelidade.pontos;

import java.util.Objects;
import java.util.UUID;

public class ContaPontosServico {

    private final ContaPontosRepositorio repositorio;
    private final ExpiracaoPontosNotificador notificador;
    private TransacaoPontosRepositorio transacaoRepositorio;

    public ContaPontosServico(ContaPontosRepositorio repositorio) {
        this(repositorio, (participanteId, pontosExpirados) -> { });
    }

    public ContaPontosServico(ContaPontosRepositorio repositorio, ExpiracaoPontosNotificador notificador) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
        this.notificador = Objects.requireNonNull(notificador, "Notificador e obrigatorio");
    }

    public void setTransacaoRepositorio(TransacaoPontosRepositorio transacaoRepositorio) {
        this.transacaoRepositorio = transacaoRepositorio;
    }

    private void registrar(UUID participanteId, TipoTransacaoPontos tipo, int pontos,
                           String descricao, UUID referenciaId) {
        if (transacaoRepositorio == null || pontos <= 0) return;
        transacaoRepositorio.salvar(new TransacaoPontos(
                TransacaoPontosId.novo(), participanteId, tipo, pontos, descricao, referenciaId));
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
        creditarPorPresenca(participanteId, pontosBase, eventoEncerrado, checkInRealizado, estrategia, null, null);
    }

    public void creditarPorPresenca(UUID participanteId, int pontosBase,
                                    boolean eventoEncerrado, boolean checkInRealizado,
                                    EstrategiaGanhoPontos estrategia,
                                    UUID eventoIdReferencia, String descricao) {
        Objects.requireNonNull(estrategia, "Estratégia de ganho de pontos é obrigatória");
        if (!eventoEncerrado || !checkInRealizado) {
            throw new IllegalStateException(
                    "Pontos só podem ser creditados após check-in em evento encerrado");
        }
        int pontosCalculados = estrategia.calcular(pontosBase);
        ContaPontos conta = obterOuCriar(participanteId);
        conta.creditarPorPresenca(pontosCalculados);
        repositorio.salvar(conta);
        registrar(participanteId, TipoTransacaoPontos.GANHO_PRESENCA, pontosCalculados,
                descricao != null ? descricao : "Ganho por presença em evento", eventoIdReferencia);
    }

    public void debitar(UUID participanteId, int pontos) {
        debitar(participanteId, pontos, null, null);
    }

    public void debitar(UUID participanteId, int pontos, UUID recompensaIdReferencia, String descricao) {
        ContaPontos conta = repositorio.buscarPorParticipanteId(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Conta de pontos não encontrada"));
        conta.debitar(pontos);
        repositorio.salvar(conta);
        registrar(participanteId, TipoTransacaoPontos.RESGATE_RECOMPENSA, pontos,
                descricao != null ? descricao : "Resgate de recompensa", recompensaIdReferencia);
    }

    public void expirarPontos(UUID participanteId, int pontosExpirados) {
        ContaPontos conta = repositorio.buscarPorParticipanteId(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Conta de pontos não encontrada"));
        conta.expirarPontos(pontosExpirados);
        repositorio.salvar(conta);
        if (pontosExpirados > 0) {
            notificador.notificarExpiracao(participanteId, pontosExpirados);
            registrar(participanteId, TipoTransacaoPontos.EXPIRACAO, pontosExpirados,
                    "Pontos expirados por inatividade", null);
        }
    }

    public int consultarSaldo(UUID participanteId) {
        return repositorio.buscarPorParticipanteId(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Conta de pontos não encontrada"))
                .getSaldo();
    }
}