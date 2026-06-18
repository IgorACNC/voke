package br.voke.controle;

import br.voke.aplicacao.inscricao.AdicionarAoCarrinhoCasoDeUso;
import br.voke.aplicacao.inscricao.AplicarCupomCarrinhoCasoDeUso;
import br.voke.aplicacao.inscricao.ConsultarCarrinhoCasoDeUso;
import br.voke.aplicacao.inscricao.FinalizarCompraCasoDeUso;
import br.voke.aplicacao.inscricao.RemoverCupomCarrinhoCasoDeUso;
import br.voke.aplicacao.inscricao.RemoverDoCarrinhoCasoDeUso;
import br.voke.dominio.inscricao.carrinho.Carrinho;
import br.voke.dominio.inscricao.carrinho.ItemCarrinho;
import br.voke.dominio.inscricao.carrinho.MetodoPagamento;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/carrinho")
@PreAuthorize("hasRole('PARTICIPANTE')")
public class CarrinhoController {

    private final AdicionarAoCarrinhoCasoDeUso adicionarItem;
    private final RemoverDoCarrinhoCasoDeUso removerItem;
    private final AplicarCupomCarrinhoCasoDeUso aplicarCupom;
    private final RemoverCupomCarrinhoCasoDeUso removerCupom;
    private final FinalizarCompraCasoDeUso finalizarCompra;
    private final ConsultarCarrinhoCasoDeUso consultarCarrinho;

    public CarrinhoController(AdicionarAoCarrinhoCasoDeUso adicionarItem,
                              RemoverDoCarrinhoCasoDeUso removerItem,
                              AplicarCupomCarrinhoCasoDeUso aplicarCupom,
                              RemoverCupomCarrinhoCasoDeUso removerCupom,
                              FinalizarCompraCasoDeUso finalizarCompra,
                              ConsultarCarrinhoCasoDeUso consultarCarrinho) {
        this.adicionarItem = adicionarItem;
        this.removerItem = removerItem;
        this.aplicarCupom = aplicarCupom;
        this.removerCupom = removerCupom;
        this.finalizarCompra = finalizarCompra;
        this.consultarCarrinho = consultarCarrinho;
    }

    record AdicionarItemReq(UUID participanteId, UUID eventoId, String nomeEvento,
                             int quantidade, BigDecimal precoUnitario) {}

    record CupomReq(UUID participanteId, String codigoCupom, String cpfParticipante) {}

    record FinalizarReq(UUID participanteId, String metodoPagamento) {}

    record ItemResp(UUID eventoId, String nomeEvento, int quantidade,
                    BigDecimal precoUnitario, BigDecimal subtotal) {}

    record CarrinhoResp(UUID participanteId, List<ItemResp> itens, String cupomAplicado,
                        BigDecimal descontoCupom, LocalDateTime criadoEm, boolean expirado) {}

    record FinalizarResp(BigDecimal total, List<UUID> inscricoesIds) {}

    record ErroResp(String mensagem) {}

    @GetMapping("/{participanteId}")
    public ResponseEntity<?> consultar(@PathVariable UUID participanteId) {
        return consultarCarrinho.executar(participanteId)
                .map(c -> ResponseEntity.ok(toResp(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/itens")
    public ResponseEntity<?> adicionar(@RequestBody AdicionarItemReq req) {
        try {
            Carrinho c = adicionarItem.executar(req.participanteId(), req.eventoId(),
                    req.nomeEvento(), req.quantidade(), req.precoUnitario());
            return ResponseEntity.ok(toResp(c));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @DeleteMapping("/itens/{participanteId}/{eventoId}")
    public ResponseEntity<?> remover(@PathVariable UUID participanteId, @PathVariable UUID eventoId) {
        try {
            removerItem.executar(participanteId, eventoId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PostMapping("/cupom")
    public ResponseEntity<?> aplicarCupom(@RequestBody CupomReq req) {
        try {
            Carrinho c = aplicarCupom.executar(req.participanteId(), req.codigoCupom(), req.cpfParticipante());
            return ResponseEntity.ok(toResp(c));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @DeleteMapping("/cupom/{participanteId}")
    public ResponseEntity<?> removerCupom(@PathVariable UUID participanteId,
                                          @RequestParam(required = false) String cpfParticipante) {
        try {
            Carrinho c = removerCupom.executar(participanteId, cpfParticipante);
            return ResponseEntity.ok(toResp(c));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PostMapping("/finalizar")
    public ResponseEntity<?> finalizar(@RequestBody FinalizarReq req) {
        try {
            MetodoPagamento metodo = MetodoPagamento.valueOf(req.metodoPagamento().toUpperCase());
            FinalizarCompraCasoDeUso.Resultado resultado = finalizarCompra.executar(req.participanteId(), metodo);
            return ResponseEntity.ok(new FinalizarResp(resultado.total(), resultado.inscricoesIds()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    private CarrinhoResp toResp(Carrinho c) {
        List<ItemResp> itens = c.getItens().stream()
                .map(i -> new ItemResp(i.getEventoId(), i.getNomeEvento(),
                        i.getQuantidade(), i.getPrecoUnitario(), i.getSubtotal()))
                .toList();
        return new CarrinhoResp(c.getParticipanteId(), itens, c.getCupomAplicado(),
                c.getDescontoCupom(), c.getCriadoEm(), c.isExpirado());
    }
}
