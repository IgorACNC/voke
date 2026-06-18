package br.voke.controle;

import br.voke.aplicacao.evento.CriarCupomCasoDeUso;
import br.voke.aplicacao.evento.EditarCupomCasoDeUso;
import br.voke.aplicacao.evento.RemoverCupomCasoDeUso;
import br.voke.aplicacao.evento.UtilizarCupomCasoDeUso;
import br.voke.dominio.evento.cupom.Cupom;
import br.voke.dominio.evento.cupom.CupomId;
import br.voke.dominio.evento.cupom.CupomRepositorio;
import br.voke.dominio.evento.cupom.CupomServico;
import br.voke.dominio.evento.cupom.TipoDesconto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cupons")
public class CupomController {

    private final CriarCupomCasoDeUso criarCupom;
    private final EditarCupomCasoDeUso editarCupom;
    private final RemoverCupomCasoDeUso removerCupom;
    private final UtilizarCupomCasoDeUso utilizarCupom;
    private final CupomServico cupomServico;
    private final CupomRepositorio cupomRepositorio;

    public CupomController(CriarCupomCasoDeUso criarCupom, EditarCupomCasoDeUso editarCupom,
                           RemoverCupomCasoDeUso removerCupom, UtilizarCupomCasoDeUso utilizarCupom,
                           CupomServico cupomServico, CupomRepositorio cupomRepositorio) {
        this.criarCupom = criarCupom;
        this.editarCupom = editarCupom;
        this.removerCupom = removerCupom;
        this.utilizarCupom = utilizarCupom;
        this.cupomServico = cupomServico;
        this.cupomRepositorio = cupomRepositorio;
    }

    record CriarCupomReq(String codigo, BigDecimal desconto, String tipoDesconto,
                          UUID organizadorId, UUID eventoId, int quantidadeMaxima) {}
    record CriarCupomGlobalReq(String codigo, BigDecimal desconto, String tipoDesconto,
                                int quantidadeMaxima) {}
    record EditarCupomReq(BigDecimal novoDesconto, int novaQuantidade) {}
    record AlterarAtivoReq(boolean ativo) {}
    record UtilizarCupomReq(String codigo, String cpfParticipante) {}

    @GetMapping("/meus")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<List<CupomResp>> listarMeus(@RequestParam UUID organizadorId) {
        return ResponseEntity.ok(cupomRepositorio.buscarPorOrganizador(organizadorId).stream()
                .map(CupomController::toResp).toList());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CupomResp>> listarTodos() {
        return ResponseEntity.ok(cupomRepositorio.buscarTodos().stream()
                .map(CupomController::toResp).toList());
    }

    @GetMapping("/globais")
    public ResponseEntity<List<CupomResp>> listarGlobais() {
        return ResponseEntity.ok(cupomRepositorio.buscarGlobais().stream()
                .map(CupomController::toResp).toList());
    }

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> criar(@RequestBody CriarCupomReq req) {
        try {
            if (req.organizadorId() == null) {
                return ResponseEntity.badRequest().body(new ErroResp("organizadorId é obrigatório"));
            }
            TipoDesconto tipo = parseTipo(req.tipoDesconto());
            Cupom c = criarCupom.executar(req.codigo(), req.desconto(), tipo,
                    req.organizadorId(), req.eventoId(), req.quantidadeMaxima());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResp(c));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PostMapping("/global")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> criarGlobal(@RequestBody CriarCupomGlobalReq req) {
        try {
            TipoDesconto tipo = parseTipo(req.tipoDesconto());
            Cupom c = criarCupom.executar(req.codigo(), req.desconto(), tipo,
                    null, null, req.quantidadeMaxima());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResp(c));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZADOR','ADMIN')")
    public ResponseEntity<?> editar(@PathVariable UUID id, @RequestBody EditarCupomReq req) {
        try {
            editarCupom.executar(id, req.novoDesconto(), req.novaQuantidade());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/ativo")
    @PreAuthorize("hasAnyRole('ORGANIZADOR','ADMIN')")
    public ResponseEntity<?> alterarAtivo(@PathVariable UUID id, @RequestBody AlterarAtivoReq req) {
        try {
            cupomServico.alternarAtivo(new CupomId(id), req.ativo());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZADOR','ADMIN')")
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

    private static TipoDesconto parseTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) return TipoDesconto.FIXO;
        try { return TipoDesconto.valueOf(tipo.toUpperCase()); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de desconto inválido (use FIXO ou PERCENTUAL)");
        }
    }

    private static CupomResp toResp(Cupom c) {
        return new CupomResp(
                c.getId().getValor().toString(),
                c.getCodigo(),
                c.getDesconto(),
                c.getTipoDesconto().name(),
                c.getOrganizadorId() == null ? null : c.getOrganizadorId().toString(),
                c.getEventoId() == null ? null : c.getEventoId().toString(),
                c.getQuantidadeMaxima(),
                c.getQuantidadeUtilizada(),
                c.isAtivo(),
                c.isGlobal());
    }

    record CupomResp(String id, String codigo, BigDecimal desconto, String tipoDesconto,
                     String organizadorId, String eventoId,
                     int quantidadeMaxima, int quantidadeUtilizada,
                     boolean ativo, boolean global) {}
    record DescontoResp(BigDecimal desconto) {}
    record ErroResp(String mensagem) {}
}
