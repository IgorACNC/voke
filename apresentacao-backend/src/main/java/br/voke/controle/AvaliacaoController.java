package br.voke.controle;

import br.voke.aplicacao.evento.AvaliarEventoCasoDeUso;
import br.voke.dominio.evento.avaliacao.Avaliacao;
import br.voke.dominio.evento.avaliacao.AvaliacaoId;
import br.voke.dominio.evento.avaliacao.AvaliacaoRepositorio;
import br.voke.dominio.evento.avaliacao.AvaliacaoServico;
import br.voke.dominio.evento.evento.Evento;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/avaliacoes")
@PreAuthorize("hasRole('PARTICIPANTE')")
public class AvaliacaoController {

    private final AvaliarEventoCasoDeUso avaliarEvento;
    private final AvaliacaoServico servico;
    private final AvaliacaoRepositorio repositorio;

    public AvaliacaoController(AvaliarEventoCasoDeUso avaliarEvento,
                               AvaliacaoServico servico,
                               AvaliacaoRepositorio repositorio) {
        this.avaliarEvento = avaliarEvento;
        this.servico = servico;
        this.repositorio = repositorio;
    }

    record AvaliarReq(UUID participanteId, UUID eventoId, int nota, String comentario) {}
    record EditarReq(int nota, String comentario) {}
    record AvaliacaoResp(String id, String participanteId, String eventoId, int nota, String comentario) {}
    record EventoAvaliavelResp(String id, String nome, String local, LocalDateTime dataHoraFim, boolean avaliado) {}
    record ErroResp(String mensagem) {}

    @PostMapping
    public ResponseEntity<?> avaliar(@RequestBody AvaliarReq req) {
        try {
            Avaliacao avaliacao = avaliarEvento.executar(req.participanteId(), req.eventoId(),
                    req.nota(), req.comentario());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResp(avaliacao));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @GetMapping("/eventos-avaliaveis")
    public ResponseEntity<List<EventoAvaliavelResp>> listarEventosAvaliaveis(@RequestParam UUID participanteId) {
        List<EventoAvaliavelResp> eventos = avaliarEvento.listarEventosAvaliaveis(participanteId).stream()
                .map(e -> toEventoResp(e, participanteId))
                .toList();
        return ResponseEntity.ok(eventos);
    }

    @GetMapping
    public ResponseEntity<?> buscar(@RequestParam UUID participanteId, @RequestParam UUID eventoId) {
        return repositorio.buscarPorParticipanteEEvento(participanteId, eventoId)
                .map(a -> ResponseEntity.ok(toResp(a)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable UUID id, @RequestBody EditarReq req) {
        try {
            servico.editar(new AvaliacaoId(id), req.nota(), req.comentario());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable UUID id) {
        try {
            servico.remover(new AvaliacaoId(id));
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    private AvaliacaoResp toResp(Avaliacao a) {
        return new AvaliacaoResp(a.getId().getValor().toString(), a.getParticipanteId().toString(),
                a.getEventoId().toString(), a.getNota(), a.getComentario());
    }

    private EventoAvaliavelResp toEventoResp(Evento e, UUID participanteId) {
        boolean avaliado = repositorio.buscarPorParticipanteEEvento(participanteId, e.getId().getValor()).isPresent();
        return new EventoAvaliavelResp(e.getId().getValor().toString(), e.getNome(), e.getLocal(),
                e.getDataHoraFim(), avaliado);
    }
}
