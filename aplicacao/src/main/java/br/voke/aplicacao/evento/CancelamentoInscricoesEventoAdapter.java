package br.voke.aplicacao.evento;

import br.voke.dominio.evento.evento.CancelamentoInscricoesEvento;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualServico;
import br.voke.dominio.inscricao.inscricao.Inscricao;
import br.voke.dominio.inscricao.inscricao.InscricaoRepositorio;
import br.voke.dominio.inscricao.inscricao.StatusInscricao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class CancelamentoInscricoesEventoAdapter implements CancelamentoInscricoesEvento {

    private final InscricaoRepositorio inscricaoRepositorio;
    private final CarteiraVirtualServico carteiraServico;

    public CancelamentoInscricoesEventoAdapter(InscricaoRepositorio inscricaoRepositorio,
                                                CarteiraVirtualServico carteiraServico) {
        Objects.requireNonNull(inscricaoRepositorio);
        Objects.requireNonNull(carteiraServico);
        this.inscricaoRepositorio = inscricaoRepositorio;
        this.carteiraServico = carteiraServico;
    }

    @Override
    public Resultado cancelarInscricoesDoEvento(UUID eventoId) {
        List<Inscricao> inscricoes = inscricaoRepositorio.buscarPorEventoId(eventoId);
        int reembolsadas = 0;
        BigDecimal total = BigDecimal.ZERO;
        for (Inscricao inscricao : inscricoes) {
            if (inscricao.getStatus() == StatusInscricao.CANCELADA) continue;
            BigDecimal valorIntegral = inscricao.getValorPago();
            carteiraServico.creditar(inscricao.getParticipanteId(), valorIntegral);
            inscricao.cancelar();
            inscricaoRepositorio.salvar(inscricao);
            reembolsadas++;
            total = total.add(valorIntegral);
        }
        return new Resultado(reembolsadas, total);
    }
}
