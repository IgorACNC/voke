package br.voke.controle;

import br.voke.aplicacao.fidelidade.*;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualServico;
import br.voke.dominio.fidelidade.transacao.TipoTransacao;
import br.voke.dominio.fidelidade.carteira.InsercaoSaldoPadrao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fidelidade")
@PreAuthorize("hasRole('PARTICIPANTE')")
public class FidelidadeController {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final BigDecimal TAXA_CARTAO = new BigDecimal("0.0299");

    private final ConsultarSaldoPontosCasoDeUso consultarPontos;
    private final ConsultarSaldoCarteiraVirtualCasoDeUso consultarCarteira;
    private final AdicionarSaldoCasoDeUso adicionarSaldo;
    private final RemoverSaldoCasoDeUso removerSaldo;
    private final ConsultarExtratoCasoDeUso consultarExtrato;
    private final CarteiraVirtualServico carteiraServico;
    private final br.voke.dominio.fidelidade.pontos.TransacaoPontosRepositorio transacaoPontosRepositorio;

    public FidelidadeController(ConsultarSaldoPontosCasoDeUso consultarPontos,
                                 ConsultarSaldoCarteiraVirtualCasoDeUso consultarCarteira,
                                 AdicionarSaldoCasoDeUso adicionarSaldo,
                                 RemoverSaldoCasoDeUso removerSaldo,
                                 ConsultarExtratoCasoDeUso consultarExtrato,
                                 CarteiraVirtualServico carteiraServico,
                                 br.voke.dominio.fidelidade.pontos.TransacaoPontosRepositorio transacaoPontosRepositorio) {
        this.consultarPontos = consultarPontos;
        this.consultarCarteira = consultarCarteira;
        this.adicionarSaldo = adicionarSaldo;
        this.removerSaldo = removerSaldo;
        this.consultarExtrato = consultarExtrato;
        this.carteiraServico = carteiraServico;
        this.transacaoPontosRepositorio = transacaoPontosRepositorio;
    }

    @GetMapping("/pontos/{participanteId}")
    public ResponseEntity<SaldoPontosResp> consultarPontos(@PathVariable UUID participanteId) {
        return ResponseEntity.ok(new SaldoPontosResp(participanteId, consultarPontos.executar(participanteId)));
    }

    @GetMapping("/carteira/{participanteId}")
    public ResponseEntity<?> consultarCarteira(@PathVariable UUID participanteId) {
        try {
            BigDecimal total = consultarCarteira.executar(participanteId);
            BigDecimal real = carteiraServico.consultarSaldoReal(participanteId);
            BigDecimal promocional = carteiraServico.consultarSaldoPromocional(participanteId);
            return ResponseEntity.ok(new SaldoCarteiraResp(participanteId, total, real, promocional));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResp("Nao foi possivel consultar a carteira."));
        }
    }

    @PostMapping("/carteira/adicionar")
    public ResponseEntity<Void> adicionarSaldo(@RequestBody SaldoReq req) {
        BigDecimal valorLiquido = "CARTAO_CREDITO".equals(req.metodoPagamento())
                ? req.valor().multiply(BigDecimal.ONE.subtract(TAXA_CARTAO))
                        .setScale(2, RoundingMode.FLOOR)
                : req.valor();
        adicionarSaldo.executar(req.participanteId(), valorLiquido, new InsercaoSaldoPadrao());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/carteira/remover")
    public ResponseEntity<Void> removerSaldo(@RequestBody SaldoReq req) {
        removerSaldo.executar(req.participanteId(), req.valor());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/carteira/{participanteId}/extrato")
    public ResponseEntity<List<TransacaoResp>> consultarExtrato(@PathVariable UUID participanteId) {
        List<TransacaoResp> extrato = consultarExtrato.executar(participanteId).stream()
                .map(t -> new TransacaoResp(
                        t.getId().getValor(),
                        t.getTipo().name(),
                        t.getValor(),
                        t.getDescricao(),
                        t.getDataHora().format(FORMATTER),
                        t.getTipo() == TipoTransacao.DEPOSITO
                                || t.getTipo() == TipoTransacao.CREDITO_BONUS
                                || t.getTipo() == TipoTransacao.ESTORNO
                                ? "ENTRADA" : "SAIDA"))
                .toList();
        return ResponseEntity.ok(extrato);
    }

    @GetMapping("/pontos/{participanteId}/extrato")
    public ResponseEntity<List<TransacaoPontosResp>> consultarExtratoPontos(@PathVariable UUID participanteId) {
        List<TransacaoPontosResp> extrato = transacaoPontosRepositorio.buscarPorParticipanteId(participanteId).stream()
                .map(t -> new TransacaoPontosResp(
                        t.getId().getValor(),
                        t.getTipo().name(),
                        t.getPontos(),
                        t.getDescricao(),
                        t.getDataHora().format(FORMATTER),
                        t.getDirecao(),
                        t.getReferenciaId()))
                .toList();
        return ResponseEntity.ok(extrato);
    }

    record SaldoReq(UUID participanteId, BigDecimal valor, String metodoPagamento) {}
    record SaldoPontosResp(UUID participanteId, int saldo) {}
    record SaldoCarteiraResp(UUID participanteId, BigDecimal saldo,
                              BigDecimal saldoReal, BigDecimal saldoPromocional) {}
    record TransacaoResp(UUID id, String tipo, BigDecimal valor, String descricao,
                         String dataHora, String direcao) {}
    record TransacaoPontosResp(UUID id, String tipo, int pontos, String descricao,
                                String dataHora, String direcao, UUID referenciaId) {}
    record ErroResp(String mensagem) {}
}
