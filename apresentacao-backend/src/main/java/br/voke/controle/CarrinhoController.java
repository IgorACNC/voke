package br.voke.controle;

import br.voke.aplicacao.inscricao.AdicionarAoCarrinhoCasoDeUso;
import br.voke.aplicacao.inscricao.AplicarCupomCarrinhoCasoDeUso;
import br.voke.aplicacao.inscricao.FinalizarCompraCasoDeUso;
import br.voke.aplicacao.inscricao.RemoverDoCarrinhoCasoDeUso;
import br.voke.dominio.inscricao.carrinho.MetodoPagamento;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/carrinho")
@PreAuthorize("hasRole('PARTICIPANTE')")
public class CarrinhoController {

    private final AdicionarAoCarrinhoCasoDeUso adicionarItem;
    private final RemoverDoCarrinhoCasoDeUso removerItem;
    private final AplicarCupomCarrinhoCasoDeUso aplicarCupom;
    private final FinalizarCompraCasoDeUso finalizarCompra;

    public CarrinhoController(AdicionarAoCarrinhoCasoDeUso adicionarItem,
                              RemoverDoCarrinhoCasoDeUso removerItem,
                              AplicarCupomCarrinhoCasoDeUso aplicarCupom,
                              FinalizarCompraCasoDeUso finalizarCompra) {
        this.adicionarItem = adicionarItem;
        this.removerItem = removerItem;
        this.aplicarCupom = aplicarCupom;
        this.finalizarCompra = finalizarCompra;
    }

    record AdicionarItemReq(UUID participanteId, UUID eventoId, String nomeEvento,
                             int quantidade, BigDecimal precoUnitario) {}
    record CupomReq(UUID participanteId, String codigoCupom, String cpfParticipante) {}
    record FinalizarReq(UUID participanteId, String metodoPagamento) {}

    @PostMapping("/itens")
    public ResponseEntity<?> adicionar(@RequestBody AdicionarItemReq req) {
        try {
            var c = adicionarItem.executar(req.participanteId(), req.eventoId(),
                    req.nomeEvento(), req.quantidade(), req.precoUnitario());
            return ResponseEntity.ok(c);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @DeleteMapping("/itens/{participanteId}/{eventoId}")
    public ResponseEntity<?> remover(@PathVariable UUID participanteId, @PathVariable UUID eventoId) {
        try {
            removerItem.executar(participanteId, eventoId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PostMapping("/cupom")
    public ResponseEntity<?> aplicarCupom(@RequestBody CupomReq req) {
        try {
            aplicarCupom.executar(req.participanteId(), req.codigoCupom(), req.cpfParticipante());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PostMapping("/finalizar")
    public ResponseEntity<?> finalizar(@RequestBody FinalizarReq req) {
        try {
            MetodoPagamento metodo = MetodoPagamento.valueOf(req.metodoPagamento().toUpperCase());
            BigDecimal total = finalizarCompra.executar(req.participanteId(), metodo);
            return ResponseEntity.ok(new TotalResp(total));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    record TotalResp(BigDecimal total) {}
    record ErroResp(String mensagem) {}
}
