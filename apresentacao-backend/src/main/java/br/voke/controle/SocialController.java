package br.voke.controle;

import br.voke.aplicacao.pessoa.*;
import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.pessoa.amizade.*;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/social")
@PreAuthorize("hasRole('PARTICIPANTE')")
public class SocialController {

    private final SolicitarAmizadeCasoDeUso solicitarAmizade;
    private final AceitarAmizadeCasoDeUso aceitarAmizade;
    private final RecusarAmizadeCasoDeUso recusarAmizade;
    private final DesfazerAmizadeCasoDeUso desfazerAmizade;
    private final CriarComunidadeCasoDeUso criarComunidade;
    private final AmizadeRepositorio amizadeRepositorio;
    private final ComunidadeAmigosRepositorio comunidadeRepositorio;
    private final ParticipanteRepositorio participanteRepositorio;

    public SocialController(SolicitarAmizadeCasoDeUso solicitarAmizade,
                            AceitarAmizadeCasoDeUso aceitarAmizade,
                            RecusarAmizadeCasoDeUso recusarAmizade,
                            DesfazerAmizadeCasoDeUso desfazerAmizade,
                            CriarComunidadeCasoDeUso criarComunidade,
                            AmizadeRepositorio amizadeRepositorio,
                            ComunidadeAmigosRepositorio comunidadeRepositorio,
                            ParticipanteRepositorio participanteRepositorio) {
        this.solicitarAmizade = solicitarAmizade;
        this.aceitarAmizade = aceitarAmizade;
        this.recusarAmizade = recusarAmizade;
        this.desfazerAmizade = desfazerAmizade;
        this.criarComunidade = criarComunidade;
        this.amizadeRepositorio = amizadeRepositorio;
        this.comunidadeRepositorio = comunidadeRepositorio;
        this.participanteRepositorio = participanteRepositorio;
    }

    record SolicitarReq(UUID solicitanteId, UUID receptorId) {}
    record CriarComunidadeReq(UUID criadorId, String nome) {}
    record MembroReq(UUID participanteId) {}
    record EventoReq(UUID eventoId) {}
    record ParticipanteResp(String id, String nome, String email) {}
    record AmizadeResp(String id, String solicitanteId, String receptorId, String status, ParticipanteResp amigo) {}
    record ComunidadeResp(String id, String nome, String criadorId, List<String> membros, List<String> eventosCompartilhados) {}
    record ErroResp(String mensagem) {}

    @PostMapping("/amizades")
    public ResponseEntity<?> solicitar(@RequestBody SolicitarReq req) {
        try {
            Amizade amizade = solicitarAmizade.executar(req.solicitanteId(), req.receptorId());
            return ResponseEntity.status(HttpStatus.CREATED).body(toAmizadeResp(amizade, new ParticipanteId(req.solicitanteId())));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @GetMapping("/amizades")
    public List<AmizadeResp> listarAmizades(@RequestParam UUID participanteId) {
        ParticipanteId id = new ParticipanteId(participanteId);
        return amizadeRepositorio.buscarPorParticipante(id).stream()
                .map(a -> toAmizadeResp(a, id)).toList();
    }

    @GetMapping("/participantes")
    public List<ParticipanteResp> buscarParticipantes(@RequestParam String termo,
                                                      @RequestParam UUID participanteAtualId) {
        String busca = termo == null ? "" : termo.trim();
        if (busca.length() < 2) {
            return List.of();
        }
        return participanteRepositorio.buscarPorNomeOuEmail(busca, 10).stream()
                .filter(p -> !p.getId().getValor().equals(participanteAtualId))
                .map(this::toParticipanteResp)
                .toList();
    }

    @PutMapping("/amizades/{id}/aceitar")
    public ResponseEntity<?> aceitar(@PathVariable UUID id) {
        try {
            aceitarAmizade.executar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PutMapping("/amizades/{id}/recusar")
    public ResponseEntity<?> recusar(@PathVariable UUID id) {
        try {
            recusarAmizade.executar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PutMapping("/amizades/{id}/desfazer")
    public ResponseEntity<?> desfazer(@PathVariable UUID id) {
        try {
            desfazerAmizade.executar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PostMapping("/comunidades")
    public ResponseEntity<?> criarComunidade(@RequestBody CriarComunidadeReq req) {
        try {
            ComunidadeAmigos comunidade = criarComunidade.executar(req.criadorId(), req.nome());
            return ResponseEntity.status(HttpStatus.CREATED).body(toComunidadeResp(comunidade));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @GetMapping("/comunidades")
    public List<ComunidadeResp> listarComunidades(@RequestParam UUID criadorId) {
        return comunidadeRepositorio.buscarPorCriador(new ParticipanteId(criadorId)).stream()
                .map(this::toComunidadeResp).toList();
    }

    @PostMapping("/comunidades/{id}/membros")
    public ResponseEntity<?> adicionarMembro(@PathVariable UUID id, @RequestBody MembroReq req) {
        try {
            ComunidadeAmigos comunidade = comunidadeRepositorio.buscarPorId(new ComunidadeAmigosId(id))
                    .orElseThrow(() -> new IllegalArgumentException("Comunidade nao encontrada"));
            comunidade.adicionarMembro(new ParticipanteId(req.participanteId()));
            comunidadeRepositorio.salvar(comunidade);
            return ResponseEntity.ok(toComunidadeResp(comunidade));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PostMapping("/comunidades/{id}/eventos")
    public ResponseEntity<?> compartilharEvento(@PathVariable UUID id, @RequestBody EventoReq req) {
        try {
            ComunidadeAmigos comunidade = comunidadeRepositorio.buscarPorId(new ComunidadeAmigosId(id))
                    .orElseThrow(() -> new IllegalArgumentException("Comunidade nao encontrada"));
            comunidade.compartilharEvento(req.eventoId());
            comunidadeRepositorio.salvar(comunidade);
            return ResponseEntity.ok(toComunidadeResp(comunidade));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    private ParticipanteResp toParticipanteResp(Participante p) {
        return new ParticipanteResp(p.getId().getValor().toString(), p.getNome().getValor(), p.getEmail().getValor());
    }

    private AmizadeResp toAmizadeResp(Amizade amizade, ParticipanteId atualId) {
        ParticipanteId amigoId = amizade.getSolicitanteId().equals(atualId)
                ? amizade.getReceptorId() : amizade.getSolicitanteId();
        ParticipanteResp amigo = participanteRepositorio.buscarPorId(amigoId).map(this::toParticipanteResp).orElse(null);
        return new AmizadeResp(amizade.getId().getValor().toString(),
                amizade.getSolicitanteId().getValor().toString(),
                amizade.getReceptorId().getValor().toString(),
                amizade.getStatus().name(), amigo);
    }

    private ComunidadeResp toComunidadeResp(ComunidadeAmigos c) {
        return new ComunidadeResp(c.getId().getValor().toString(), c.getNome().getValor(),
                c.getCriadorId().getValor().toString(),
                c.getMembros().stream().map(m -> m.getValor().toString()).toList(),
                c.getEventoCompartilhadoIds().stream().map(UUID::toString).toList());
    }
}
