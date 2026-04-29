package br.voke.dominio.inscricao.inscricao;

import br.voke.dominio.inscricao.excecao.ConflitoDeAgendaException;
import br.voke.dominio.inscricao.excecao.IdadeMinimaEventoException;
import br.voke.dominio.inscricao.excecao.LimiteIngressosCpfException;
import br.voke.dominio.inscricao.excecao.VagasEsgotadasException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class InscricaoServico {

    private final InscricaoRepositorio repositorio;

    public InscricaoServico(InscricaoRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
    }

    public Inscricao realizar(UUID participanteId, UUID eventoId, BigDecimal valorIngresso,
                              int idadeParticipante, int idadeMinimaEvento,
                              boolean eventoAtivo, boolean possuiVagas,
                              LocalDateTime eventoInicio, LocalDateTime eventoFim,
                              int limitePorCpf) {
        if (idadeParticipante < idadeMinimaEvento) {
            throw new IdadeMinimaEventoException();
        }
        if (!eventoAtivo || !possuiVagas) {
            throw new VagasEsgotadasException();
        }
        if (repositorio.existeConflitoDeHorario(participanteId, eventoInicio, eventoFim)) {
            throw new ConflitoDeAgendaException();
        }
        int inscricoesExistentes = repositorio.contarPorParticipanteEEvento(participanteId, eventoId);
        if (inscricoesExistentes >= limitePorCpf) {
            throw new LimiteIngressosCpfException();
        }
        Inscricao inscricao = new Inscricao(InscricaoId.novo(), participanteId, eventoId, valorIngresso);
        repositorio.salvar(inscricao);
        return inscricao;
    }

    public BigDecimal cancelar(InscricaoId id, LocalDateTime dataEvento) {
        Inscricao inscricao = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Inscrição não encontrada"));
        BigDecimal devolucao = inscricao.calcularDevolucao(dataEvento);
        inscricao.cancelar();
        repositorio.salvar(inscricao);
        return devolucao;
    }
    public void realizarCheckIn(InscricaoId id, boolean eventoEmAndamento) {
        Inscricao inscricao = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Inscricao nao encontrada"));
        if (!eventoEmAndamento) {
            throw new IllegalStateException("Check-in so pode ser realizado com o evento em andamento");
        }
        if (!inscricao.estaConfirmada()) {
            throw new IllegalStateException("Check-in exige inscricao confirmada");
        }
        inscricao.realizarCheckIn();
        repositorio.salvar(inscricao);
    }
}
