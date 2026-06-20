package br.voke.controle;

import br.voke.aplicacao.evento.CriarPerguntaFaqCasoDeUso;
import br.voke.aplicacao.evento.EditarPerguntaFaqCasoDeUso;
import br.voke.aplicacao.evento.ExcluirPerguntaFaqCasoDeUso;
import br.voke.aplicacao.evento.ListarFaqDoEventoCasoDeUso;
import br.voke.aplicacao.evento.ReordenarFaqCasoDeUso;
import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.excecao.OrganizadorNaoDonoDoEventoException;
import br.voke.dominio.evento.faq.PerguntaFrequente;
import br.voke.seguranca.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/eventos/{eventoId}/faq")
public class PerguntaFaqController {

    private final CriarPerguntaFaqCasoDeUso criar;
    private final EditarPerguntaFaqCasoDeUso editar;
    private final ExcluirPerguntaFaqCasoDeUso excluir;
    private final ReordenarFaqCasoDeUso reordenar;
    private final ListarFaqDoEventoCasoDeUso listar;
    private final EventoRepositorio eventoRepositorio;
    private final JwtUtil jwtUtil;

    public PerguntaFaqController(CriarPerguntaFaqCasoDeUso criar,
                                  EditarPerguntaFaqCasoDeUso editar,
                                  ExcluirPerguntaFaqCasoDeUso excluir,
                                  ReordenarFaqCasoDeUso reordenar,
                                  ListarFaqDoEventoCasoDeUso listar,
                                  EventoRepositorio eventoRepositorio,
                                  JwtUtil jwtUtil) {
        this.criar = criar;
        this.editar = editar;
        this.excluir = excluir;
        this.reordenar = reordenar;
        this.listar = listar;
        this.eventoRepositorio = eventoRepositorio;
        this.jwtUtil = jwtUtil;
    }

    record PerguntaReq(String pergunta, String resposta) {}
    record ReordenarReq(List<UUID> ordem) {}
    record PerguntaResp(String id, String eventoId, String pergunta, String resposta, int posicao) {}
    record ErroResp(String mensagem) {}

    @GetMapping
    public ResponseEntity<?> listar(@PathVariable UUID eventoId) {
        List<PerguntaResp> resp = listar.executar(eventoId).stream()
                .map(PerguntaFaqController::toResp)
                .toList();
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> criar(@PathVariable UUID eventoId,
                                    @RequestBody PerguntaReq req,
                                    HttpServletRequest httpReq) {
        try {
            validarDono(eventoId, httpReq);
            PerguntaFrequente p = criar.executar(eventoId, req.pergunta(), req.resposta());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResp(p));
        } catch (OrganizadorNaoDonoDoEventoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErroResp(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> editar(@PathVariable UUID eventoId,
                                     @PathVariable UUID id,
                                     @RequestBody PerguntaReq req,
                                     HttpServletRequest httpReq) {
        try {
            validarDono(eventoId, httpReq);
            PerguntaFrequente p = editar.executar(id, req.pergunta(), req.resposta());
            return ResponseEntity.ok(toResp(p));
        } catch (OrganizadorNaoDonoDoEventoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErroResp(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> excluir(@PathVariable UUID eventoId,
                                      @PathVariable UUID id,
                                      HttpServletRequest httpReq) {
        try {
            validarDono(eventoId, httpReq);
            excluir.executar(id);
            return ResponseEntity.noContent().build();
        } catch (OrganizadorNaoDonoDoEventoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErroResp(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PutMapping("/ordem")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> reordenar(@PathVariable UUID eventoId,
                                        @RequestBody ReordenarReq req,
                                        HttpServletRequest httpReq) {
        try {
            validarDono(eventoId, httpReq);
            List<PerguntaResp> resp = reordenar.executar(eventoId, req.ordem()).stream()
                    .map(PerguntaFaqController::toResp)
                    .toList();
            return ResponseEntity.ok(resp);
        } catch (OrganizadorNaoDonoDoEventoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErroResp(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    private void validarDono(UUID eventoId, HttpServletRequest httpReq) {
        UUID organizadorId = idAutenticado(httpReq);
        Evento evento = eventoRepositorio.buscarPorId(new EventoId(eventoId))
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
        if (!evento.getOrganizadorId().equals(organizadorId)) {
            throw new OrganizadorNaoDonoDoEventoException();
        }
    }

    private UUID idAutenticado(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String idStr = jwtUtil.extrairClaims(token).get("id", String.class);
        return UUID.fromString(idStr);
    }

    private static PerguntaResp toResp(PerguntaFrequente p) {
        return new PerguntaResp(
                p.getId().getValor().toString(),
                p.getEventoId().toString(),
                p.getPergunta(),
                p.getResposta(),
                p.getPosicao());
    }
}
