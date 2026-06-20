package br.voke.aplicacao.inscricao;

import br.voke.aplicacao.evento.AtualizadorEstatisticaListener;
import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualServico;
import br.voke.dominio.inscricao.carrinho.Carrinho;
import br.voke.dominio.inscricao.carrinho.CarrinhoRepositorio;
import br.voke.dominio.inscricao.carrinho.CarrinhoServico;
import br.voke.dominio.inscricao.carrinho.EstrategiaTaxa;
import br.voke.dominio.inscricao.carrinho.ItemCarrinho;
import br.voke.dominio.inscricao.carrinho.MetodoPagamento;
import br.voke.dominio.inscricao.carrinho.SemTaxa;
import br.voke.dominio.inscricao.carrinho.TaxaCartaoCredito;
import br.voke.dominio.inscricao.inscricao.Inscricao;
import br.voke.dominio.inscricao.inscricao.InscricaoServico;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;
import br.voke.dominio.fidelidade.comissao.ComissaoParceiroServico;
import br.voke.dominio.evento.cupom.CupomRepositorio;
import br.voke.dominio.pessoa.parceiro.ParceiroRepositorio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FinalizarCompraCasoDeUso {

    private final CarrinhoServico carrinhoServico;
    private final CarrinhoRepositorio carrinhoRepositorio;
    private final CarteiraVirtualServico carteiraServico;
    private final InscricaoServico inscricaoServico;
    private final EventoRepositorio eventoRepositorio;
    private final ParticipanteRepositorio participanteRepositorio;
    private final AtualizadorEstatisticaListener atualizadorEstatistica;
    private final ComissaoParceiroServico comissaoParceiroServico;
    private final CupomRepositorio cupomRepositorio;
    private final ParceiroRepositorio parceiroRepositorio;

    public FinalizarCompraCasoDeUso(CarrinhoServico carrinhoServico,
                                     CarrinhoRepositorio carrinhoRepositorio,
                                     CarteiraVirtualServico carteiraServico,
                                     InscricaoServico inscricaoServico,
                                     EventoRepositorio eventoRepositorio,
                                     ParticipanteRepositorio participanteRepositorio,
                                     AtualizadorEstatisticaListener atualizadorEstatistica,
                                     ComissaoParceiroServico comissaoParceiroServico,
                                     CupomRepositorio cupomRepositorio,
                                     ParceiroRepositorio parceiroRepositorio) {
        Objects.requireNonNull(carrinhoServico);
        Objects.requireNonNull(carrinhoRepositorio);
        Objects.requireNonNull(carteiraServico);
        Objects.requireNonNull(inscricaoServico);
        Objects.requireNonNull(eventoRepositorio);
        Objects.requireNonNull(participanteRepositorio);
        Objects.requireNonNull(atualizadorEstatistica);
        Objects.requireNonNull(comissaoParceiroServico);
        Objects.requireNonNull(cupomRepositorio);
        Objects.requireNonNull(parceiroRepositorio);
        this.carrinhoServico = carrinhoServico;
        this.carrinhoRepositorio = carrinhoRepositorio;
        this.carteiraServico = carteiraServico;
        this.inscricaoServico = inscricaoServico;
        this.eventoRepositorio = eventoRepositorio;
        this.participanteRepositorio = participanteRepositorio;
        this.atualizadorEstatistica = atualizadorEstatistica;
        this.comissaoParceiroServico = comissaoParceiroServico;
        this.cupomRepositorio = cupomRepositorio;
        this.parceiroRepositorio = parceiroRepositorio;
    }

    public Resultado executar(UUID participanteId, MetodoPagamento metodoPagamento) {
        Carrinho carrinho = carrinhoRepositorio.buscarPorParticipanteId(participanteId)
                .orElseThrow(() -> new IllegalStateException("Carrinho não encontrado"));

        if (carrinho.getItens().isEmpty()) {
            throw new IllegalStateException("Carrinho está vazio");
        }

        carrinho.validarNaoExpirado();

        EstrategiaTaxa taxa = switch (metodoPagamento) {
            case CARTAO_CREDITO -> new TaxaCartaoCredito();
            case PIX -> new SemTaxa();
        };
        BigDecimal total = carrinho.calcularTotal(taxa);

        // RN6 - pre-auth: garante saldo antes de criar inscrições/ingressos.
        BigDecimal saldoAtual = carteiraServico.consultarSaldo(participanteId);
        if (saldoAtual.compareTo(total) < 0) {
            throw new IllegalStateException("Saldo insuficiente para finalizar a compra");
        }

        Participante participante = participanteRepositorio.buscarPorId(new ParticipanteId(participanteId))
                .orElseThrow(() -> new IllegalArgumentException("Participante não encontrado"));

        BigDecimal subtotal = carrinho.getItens().stream()
                .map(ItemCarrinho::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // RN4 - debita primeiro para reservar o valor; se algo falhar adiante,
        // o @Transactional do chamador faz rollback de tudo (débito + inscrições).
        carteiraServico.debitar(participanteId, total);

        List<UUID> inscricoesIds = new ArrayList<>();

        for (ItemCarrinho item : carrinho.getItens()) {
            Evento evento = eventoRepositorio.buscarPorId(new EventoId(item.getEventoId()))
                    .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado: " + item.getEventoId()));

            BigDecimal valorPorIngresso = calcularValorPorIngresso(item, subtotal, total);

            for (int i = 0; i < item.getQuantidade(); i++) {
                Inscricao inscricao = inscricaoServico.realizar(
                        participanteId, item.getEventoId(), valorPorIngresso,
                        participante.getIdade(), evento.getIdadeMinima(),
                        evento.estaAtivo(), evento.possuiVagas(),
                        evento.getDataHoraInicio(), evento.getDataHoraFim(),
                        Integer.MAX_VALUE);
                inscricoesIds.add(inscricao.getId().getValor());
                if (evento.getLoteAtual() != null) {
                    evento.getLoteAtual().venderIngresso();
                }
                // F17 - RN03: atualiza snapshot apos cada inscricao confirmada
                atualizadorEstatistica.aoConfirmarInscricao(item.getEventoId(), valorPorIngresso);

                if (carrinho.getCupomAplicado() != null) {
                    cupomRepositorio.buscarPorCodigo(carrinho.getCupomAplicado()).ifPresent(cupom -> {
                        if (cupom.isVinculadoAParceiro()) {
                            parceiroRepositorio
                                    .buscarPorParticipante(new ParticipanteId(cupom.getParceiroId()))
                                    .stream()
                                    .findFirst()
                                    .ifPresent(parceiro -> comissaoParceiroServico.creditarComissao(
                                            parceiro.getId().getValor(),
                                            cupom.getId().getValor(),
                                            inscricao.getId().getValor(),
                                            valorPorIngresso
                                    ));
                        }
                    });
                }
            }
            eventoRepositorio.salvar(evento);
        }

        carrinhoServico.limpar(participanteId);

        return new Resultado(total, inscricoesIds);
    }

    private BigDecimal calcularValorPorIngresso(ItemCarrinho item, BigDecimal subtotal, BigDecimal total) {
        if (subtotal.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        BigDecimal proporcao = item.getSubtotal().divide(subtotal, 10, RoundingMode.HALF_UP);
        BigDecimal valorItem = total.multiply(proporcao);
        return valorItem.divide(BigDecimal.valueOf(item.getQuantidade()), 2, RoundingMode.HALF_UP);
    }

    public record Resultado(BigDecimal total, List<UUID> inscricoesIds) {}
}