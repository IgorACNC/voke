package br.voke.controle;

import br.voke.aplicacao.evento.*;
import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
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

    public EventoController(CriarEventoCasoDeUso criarEvento,
                            EditarEventoCasoDeUso editarEvento,
                            CancelarEventoCasoDeUso cancelarEvento,
                            EventoRepositorio eventoRepositorio) {
        this.criarEvento = criarEvento;
        this.editarEvento = editarEvento;
        this.cancelarEvento = cancelarEvento;
        this.eventoRepositorio = eventoRepositorio;
    }

    record CriarEventoReq(String nome, String descricao, String local,
                          LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim,
                          int capacidadeMaxima, UUID organizadorId,
                          BigDecimal precoLote, int quantidadeLote, int idadeMinima,
                          Set<UUID> categoriaIds) {}

    record EditarEventoReq(String nome, String local,
                           LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim,
                           int capacidadeMaxima) {}

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> criar(@RequestBody CriarEventoReq req) {
        try {
            Evento evento = criarEvento.executar(req.nome(), req.descricao(), req.local(),
                    req.dataHoraInicio(), req.dataHoraFim(), req.capacidadeMaxima(),
                    req.organizadorId(), req.precoLote(), req.quantidadeLote(), req.idadeMinima(),
                    req.categoriaIds());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResposta(evento));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> editar(@PathVariable UUID id, @RequestBody EditarEventoReq req) {
        try {
            editarEvento.executar(id, req.nome(), req.local(),
                    req.dataHoraInicio(), req.dataHoraFim(), req.capacidadeMaxima());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> cancelar(@PathVariable UUID id) {
        try {
            cancelarEvento.executar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable UUID id) {
        return eventoRepositorio.buscarPorId(new EventoId(id))
                .map(e -> ResponseEntity.ok(toResposta(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    private record EventoResp(String id, String nome, String descricao, String local,
                               LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim,
                               int capacidadeMaxima, String status, int idadeMinima) {}

    private EventoResp toResposta(Evento e) {
        return new EventoResp(e.getId().getValor().toString(), e.getNome(), e.getDescricao(),
                e.getLocal(), e.getDataHoraInicio(), e.getDataHoraFim(),
                e.getCapacidadeMaxima(), e.getStatus().name(), e.getIdadeMinima());
    }

    record ErroResp(String mensagem) {}
}
