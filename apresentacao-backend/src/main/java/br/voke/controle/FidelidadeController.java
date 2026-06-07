package br.voke.controle;

import br.voke.aplicacao.fidelidade.*;
import br.voke.dominio.fidelidade.carteira.InsercaoSaldoPadrao;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/fidelidade")
@PreAuthorize("hasRole('PARTICIPANTE')")
public class FidelidadeController {

    private final ConsultarSaldoPontosCasoDeUso consultarPontos;
    private final ConsultarSaldoCarteiraVirtualCasoDeUso consultarCarteira;
    private final AdicionarSaldoCasoDeUso adicionarSaldo;
    private final RemoverSaldoCasoDeUso removerSaldo;

    public FidelidadeController(ConsultarSaldoPontosCasoDeUso consultarPontos,
                                 ConsultarSaldoCarteiraVirtualCasoDeUso consultarCarteira,
                                 AdicionarSaldoCasoDeUso adicionarSaldo,
                                 RemoverSaldoCasoDeUso removerSaldo) {
        this.consultarPontos = consultarPontos;
        this.consultarCarteira = consultarCarteira;
        this.adicionarSaldo = adicionarSaldo;
        this.removerSaldo = removerSaldo;
    }

    record SaldoReq(UUID participanteId, BigDecimal valor) {}

    @GetMapping("/pontos/{participanteId}")
    public ResponseEntity<SaldoPontosResp> consultarPontos(@PathVariable UUID participanteId) {
        return ResponseEntity.ok(new SaldoPontosResp(participanteId, consultarPontos.executar(participanteId)));
    }

    @GetMapping("/carteira/{participanteId}")
    public ResponseEntity<SaldoCarteiraResp> consultarCarteira(@PathVariable UUID participanteId) {
        return ResponseEntity.ok(new SaldoCarteiraResp(participanteId, consultarCarteira.executar(participanteId)));
    }

    @PostMapping("/carteira/adicionar")
    public ResponseEntity<?> adicionarSaldo(@RequestBody SaldoReq req) {
        try {
            adicionarSaldo.executar(req.participanteId(), req.valor(), new InsercaoSaldoPadrao());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PostMapping("/carteira/remover")
    public ResponseEntity<?> removerSaldo(@RequestBody SaldoReq req) {
        try {
            removerSaldo.executar(req.participanteId(), req.valor());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    record SaldoPontosResp(UUID participanteId, int saldo) {}
    record SaldoCarteiraResp(UUID participanteId, BigDecimal saldo) {}
    record ErroResp(String mensagem) {}
}
