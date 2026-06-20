package br.voke.controle;

import br.voke.aplicacao.evento.AvaliarEventoCasoDeUso;
import br.voke.dominio.evento.avaliacao.Avaliacao;
import br.voke.dominio.evento.avaliacao.AvaliacaoId;
import br.voke.dominio.evento.avaliacao.AvaliacaoRepositorio;
import br.voke.dominio.evento.avaliacao.AvaliacaoServico;
import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoController {

    private final AvaliarEventoCasoDeUso avaliarEvento;
    private final AvaliacaoServico servico;
    private final AvaliacaoRepositorio repositorio;
    private final ParticipanteRepositorio participanteRepositorio;

    public AvaliacaoController(AvaliarEventoCasoDeUso avaliarEvento,
                               AvaliacaoServico servico,
                               AvaliacaoRepositorio repositorio,
                               ParticipanteRepositorio participanteRepositorio) {
        this.avaliarEvento = avaliarEvento;
        this.servico = servico;
        this.repositorio = repositorio;
        this.participanteRepositorio = participanteRepositorio;
    }

    record AvaliarReq(UUID participanteId, UUID eventoId, int nota, String comentario) {}
    record EditarReq(int nota, String comentario) {}
    record AvaliacaoResp(String id, String participanteId, String eventoId, int nota, String comentario) {}
    record AvaliacaoPublicaResp(String id, String participanteId, String nomeParticipante,
                                int nota, String comentario) {}
    record ResumoAvaliacaoResp(double media, int quantidade) {}
    record EventoAvaliavelResp(String id, String nome, String local, LocalDateTime dataHoraFim,
                                boolean avaliado, double media, int quantidade) {}
    record ErroResp(String mensagem) {}

    @PostMapping
    @PreAuthorize("hasRole('PARTICIPANTE')")
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
    @PreAuthorize("hasRole('PARTICIPANTE')")
    public ResponseEntity<List<EventoAvaliavelResp>> listarEventosAvaliaveis(@RequestParam UUID participanteId) {
        List<EventoAvaliavelResp> eventos = avaliarEvento.listarEventosAvaliaveis(participanteId).stream()
                .map(e -> toEventoResp(e, participanteId))
                .toList();
        return ResponseEntity.ok(eventos);
    }

    @GetMapping
    @PreAuthorize("hasRole('PARTICIPANTE')")
    public ResponseEntity<?> buscar(@RequestParam UUID participanteId, @RequestParam UUID eventoId) {
        return repositorio.buscarPorParticipanteEEvento(participanteId, eventoId)
                .map(a -> ResponseEntity.ok(toResp(a)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/evento/{eventoId}")
    @PreAuthorize("hasAnyRole('PARTICIPANTE','ORGANIZADOR','ADMIN')")
    public ResponseEntity<List<AvaliacaoPublicaResp>> listarPorEvento(@PathVariable UUID eventoId) {
        List<Avaliacao> avaliacoes = repositorio.buscarPorEventoId(eventoId);
        Map<UUID, String> nomes = avaliacoes.stream()
                .map(Avaliacao::getParticipanteId)
                .distinct()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> participanteRepositorio.buscarPorId(new ParticipanteId(id))
                                .map(p -> p.getNome().getValor())
                                .orElse("Participante")));
        List<AvaliacaoPublicaResp> resp = avaliacoes.stream()
                .map(a -> new AvaliacaoPublicaResp(
                        a.getId().getValor().toString(),
                        a.getParticipanteId().toString(),
                        nomes.getOrDefault(a.getParticipanteId(), "Participante"),
                        a.getNota(), a.getComentario()))
                .toList();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/evento/{eventoId}/resumo")
    public ResponseEntity<ResumoAvaliacaoResp> resumo(@PathVariable UUID eventoId) {
        List<Avaliacao> lista = repositorio.buscarPorEventoId(eventoId);
        double media = lista.stream().mapToInt(Avaliacao::getNota).average().orElse(0.0);
        return ResponseEntity.ok(new ResumoAvaliacaoResp(arredondar(media), lista.size()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PARTICIPANTE')")
    public ResponseEntity<?> editar(@PathVariable UUID id, @RequestBody EditarReq req) {
        try {
            servico.editar(new AvaliacaoId(id), req.nota(), req.comentario());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PARTICIPANTE')")
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
        List<Avaliacao> lista = repositorio.buscarPorEventoId(e.getId().getValor());
        double media = lista.stream().mapToInt(Avaliacao::getNota).average().orElse(0.0);
        return new EventoAvaliavelResp(e.getId().getValor().toString(), e.getNome(), e.getLocal(),
                e.getDataHoraFim(), avaliado, arredondar(media), lista.size());
    }

    private static double arredondar(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
