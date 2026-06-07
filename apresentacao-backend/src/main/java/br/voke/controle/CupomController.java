package br.voke.controle;

import br.voke.aplicacao.evento.CriarCupomCasoDeUso;
import br.voke.aplicacao.evento.EditarCupomCasoDeUso;
import br.voke.aplicacao.evento.RemoverCupomCasoDeUso;
import br.voke.aplicacao.evento.UtilizarCupomCasoDeUso;
import br.voke.dominio.evento.cupom.Cupom;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/cupons")
public class CupomController {

    private final CriarCupomCasoDeUso criarCupom;
    private final EditarCupomCasoDeUso editarCupom;
    private final RemoverCupomCasoDeUso removerCupom;
    private final UtilizarCupomCasoDeUso utilizarCupom;

    public CupomController(CriarCupomCasoDeUso criarCupom, EditarCupomCasoDeUso editarCupom,
                           RemoverCupomCasoDeUso removerCupom, UtilizarCupomCasoDeUso utilizarCupom) {
        this.criarCupom = criarCupom;
        this.editarCupom = editarCupom;
        this.removerCupom = removerCupom;
        this.utilizarCupom = utilizarCupom;
    }

    record CriarCupomReq(String codigo, BigDecimal desconto, UUID organizadorId,
                          UUID eventoId, int quantidadeMaxima) {}
    record EditarCupomReq(BigDecimal novoDesconto, int novaQuantidade) {}
    record UtilizarCupomReq(String codigo, String cpfParticipante) {}

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> criar(@RequestBody CriarCupomReq req) {
        try {
            Cupom c = criarCupom.executar(req.codigo(), req.desconto(),
                    req.organizadorId(), req.eventoId(), req.quantidadeMaxima());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CupomResp(c.getId().getValor().toString(), c.getCodigo(),
                            c.getDesconto(), c.getQuantidadeMaxima()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> editar(@PathVariable UUID id, @RequestBody EditarCupomReq req) {
        try {
            editarCupom.executar(id, req.novoDesconto(), req.novaQuantidade());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> remover(@PathVariable UUID id) {
        try {
            removerCupom.executar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PostMapping("/utilizar")
    @PreAuthorize("hasRole('PARTICIPANTE')")
    public ResponseEntity<?> utilizar(@RequestBody UtilizarCupomReq req) {
        try {
            BigDecimal desconto = utilizarCupom.executar(req.codigo(), req.cpfParticipante());
            return ResponseEntity.ok(new DescontoResp(desconto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    record CupomResp(String id, String codigo, BigDecimal desconto, int quantidadeMaxima) {}
    record DescontoResp(BigDecimal desconto) {}
    record ErroResp(String mensagem) {}
}
