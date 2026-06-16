package br.voke.controle;

import br.voke.aplicacao.evento.*;
import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.evento.Lote;
import br.voke.seguranca.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final CriarEventoCasoDeUso criarEvento;
    private final EditarEventoCasoDeUso editarEvento;
    private final CancelarEventoCasoDeUso cancelarEvento;
    private final EventoRepositorio eventoRepositorio;
    private final JwtUtil jwtUtil;

    public EventoController(CriarEventoCasoDeUso criarEvento,
                            EditarEventoCasoDeUso editarEvento,
                            CancelarEventoCasoDeUso cancelarEvento,
                            EventoRepositorio eventoRepositorio,
                            JwtUtil jwtUtil) {
        this.criarEvento = criarEvento;
        this.editarEvento = editarEvento;
        this.cancelarEvento = cancelarEvento;
        this.eventoRepositorio = eventoRepositorio;
        this.jwtUtil = jwtUtil;
    }

    record CriarEventoReq(String nome, String descricao, String local,
                          LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim,
                          int capacidadeMaxima,
                          BigDecimal precoLote, int quantidadeLote, int idadeMinima,
                          Set<UUID> categoriaIds) {}

    record EditarEventoReq(String nome, String local,
                           LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim,
                           int capacidadeMaxima) {}

    record CriarLoteReq(BigDecimal preco, int quantidade) {}

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> criar(@RequestBody CriarEventoReq req, HttpServletRequest httpReq) {
        UUID organizadorId = idAutenticado(httpReq);
        Evento evento = criarEvento.executar(req.nome(), req.descricao(), req.local(),
                req.dataHoraInicio(), req.dataHoraFim(), req.capacidadeMaxima(),
                organizadorId, req.precoLote(), req.quantidadeLote(), req.idadeMinima(),
                req.categoriaIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResposta(evento));
    }

    @GetMapping
    public ResponseEntity<?> listarAtivos() {
        List<Evento> eventos = eventoRepositorio.listarAtivos();
        return ResponseEntity.ok(eventos.stream().map(this::toResposta).toList());
    }

    @GetMapping("/meus")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> listarMeus(HttpServletRequest httpReq) {
        UUID organizadorId = idAutenticado(httpReq);
        List<Evento> eventos = eventoRepositorio.buscarPorOrganizador(organizadorId);
        return ResponseEntity.ok(eventos.stream().map(this::toResposta).toList());
    }

    @GetMapping("/organizador/{organizadorId}")
    public ResponseEntity<?> listarPorOrganizador(@PathVariable UUID organizadorId) {
        List<Evento> eventos = eventoRepositorio.buscarPorOrganizador(organizadorId);
        return ResponseEntity.ok(eventos.stream().map(this::toResposta).toList());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> editar(@PathVariable UUID id, @RequestBody EditarEventoReq req,
                                    HttpServletRequest httpReq) {
        UUID organizadorId = idAutenticado(httpReq);
        Evento evento = eventoRepositorio.buscarPorId(new EventoId(id))
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
        if (!evento.getOrganizadorId().equals(organizadorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErroResp("Acesso negado"));
        }
        editarEvento.executar(id, req.nome(), req.local(),
                req.dataHoraInicio(), req.dataHoraFim(), req.capacidadeMaxima());
        Evento atualizado = eventoRepositorio.buscarPorId(new EventoId(id)).orElseThrow();
        return ResponseEntity.ok(toResposta(atualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> cancelar(@PathVariable UUID id, HttpServletRequest httpReq) {
        UUID organizadorId = idAutenticado(httpReq);
        Evento evento = eventoRepositorio.buscarPorId(new EventoId(id))
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
        if (!evento.getOrganizadorId().equals(organizadorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErroResp("Acesso negado"));
        }
        cancelarEvento.executar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable UUID id) {
        return eventoRepositorio.buscarPorId(new EventoId(id))
                .map(e -> ResponseEntity.ok(toResposta(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/lotes")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> criarLote(@PathVariable UUID id, @RequestBody CriarLoteReq req,
                                       HttpServletRequest httpReq) {
        UUID organizadorId = idAutenticado(httpReq);
        Evento evento = eventoRepositorio.buscarPorId(new EventoId(id))
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
        if (!evento.getOrganizadorId().equals(organizadorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErroResp("Acesso negado"));
        }
        int proximoNumero = (evento.getLoteAtual() != null ? evento.getLoteAtual().getNumero() : 0) + 1;
        Lote novoLote = new Lote(proximoNumero, req.preco(), req.quantidade());
        evento.criarNovoLote(novoLote);
        eventoRepositorio.salvar(evento);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResposta(evento));
    }

    private UUID idAutenticado(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String idStr = jwtUtil.extrairClaims(token).get("id", String.class);
        return UUID.fromString(idStr);
    }

    private record LoteResp(int numero, BigDecimal preco, int quantidadeTotal,
                             int quantidadeVendida, boolean ativo) {}

    private record EventoResp(String id, String nome, String descricao, String local,
                               LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim,
                               int capacidadeMaxima, String status, int idadeMinima,
                               String organizadorId,
                               LoteResp loteAtual) {}

    private EventoResp toResposta(Evento e) {
        LoteResp lote = null;
        if (e.getLoteAtual() != null) {
            Lote l = e.getLoteAtual();
            lote = new LoteResp(l.getNumero(), l.getPreco(), l.getQuantidadeTotal(),
                    l.getQuantidadeVendida(), l.isAtivo());
        }
        return new EventoResp(e.getId().getValor().toString(), e.getNome(), e.getDescricao(),
                e.getLocal(), e.getDataHoraInicio(), e.getDataHoraFim(),
                e.getCapacidadeMaxima(), e.getStatus().name(), e.getIdadeMinima(),
                e.getOrganizadorId().toString(), lote);
    }

    record ErroResp(String mensagem) {}
}
