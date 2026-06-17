package br.voke.controle;

import br.voke.aplicacao.convite.*;
import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.inscricao.convite.Convite;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/convites")
@PreAuthorize("hasRole('PARTICIPANTE')")
public class ConviteController {

    private final EnviarConviteCasoDeUso enviarConvite;
    private final AceitarConviteCasoDeUso aceitarConvite;
    private final RejeitarConviteCasoDeUso rejeitarConvite;
    private final CancelarConviteCasoDeUso cancelarConvite;
    private final ListarConvitesRecebidosCasoDeUso listarRecebidos;
    private final ListarConvitesEnviadosCasoDeUso listarEnviados;
    private final EventoRepositorio eventoRepositorio;
    private final ParticipanteRepositorio participanteRepositorio;

    public ConviteController(EnviarConviteCasoDeUso enviarConvite,
                              AceitarConviteCasoDeUso aceitarConvite,
                              RejeitarConviteCasoDeUso rejeitarConvite,
                              CancelarConviteCasoDeUso cancelarConvite,
                              ListarConvitesRecebidosCasoDeUso listarRecebidos,
                              ListarConvitesEnviadosCasoDeUso listarEnviados,
                              EventoRepositorio eventoRepositorio,
                              ParticipanteRepositorio participanteRepositorio) {
        this.enviarConvite = enviarConvite;
        this.aceitarConvite = aceitarConvite;
        this.rejeitarConvite = rejeitarConvite;
        this.cancelarConvite = cancelarConvite;
        this.listarRecebidos = listarRecebidos;
        this.listarEnviados = listarEnviados;
        this.eventoRepositorio = eventoRepositorio;
        this.participanteRepositorio = participanteRepositorio;
    }

    record EnviarConviteReq(UUID remetenteId, String emailDestinatario, UUID eventoId) {}
    record AceitarRejeitarReq(UUID participanteId) {}
    record ConviteResp(String id, String eventoId, String eventoNome,
                       String remetenteId, String remetenteNome,
                       String destinatarioId, String destinatarioNome,
                       String status, LocalDateTime criadoEm, LocalDateTime expiraEm) {}
    record ErroResp(String mensagem) {}

    @PostMapping
    public ResponseEntity<?> enviar(@RequestBody EnviarConviteReq req) {
        try {
            Convite c = enviarConvite.executar(req.remetenteId(), req.emailDestinatario(), req.eventoId());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResp(c));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @GetMapping("/recebidos")
    public ResponseEntity<?> recebidos(@RequestParam UUID participanteId) {
        List<ConviteResp> lista = listarRecebidos.executar(participanteId).stream()
                .map(this::toResp)
                .filter(Objects::nonNull)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/enviados")
    public ResponseEntity<?> enviados(@RequestParam UUID participanteId) {
        List<ConviteResp> lista = listarEnviados.executar(participanteId).stream()
                .map(this::toResp)
                .filter(Objects::nonNull)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @PatchMapping("/{id}/aceitar")
    public ResponseEntity<?> aceitar(@PathVariable UUID id, @RequestBody AceitarRejeitarReq req) {
        try {
            aceitarConvite.executar(id, req.participanteId());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/rejeitar")
    public ResponseEntity<?> rejeitar(@PathVariable UUID id, @RequestBody AceitarRejeitarReq req) {
        try {
            rejeitarConvite.executar(id, req.participanteId());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelar(@PathVariable UUID id, @RequestParam UUID remetenteId) {
        try {
            cancelarConvite.executar(id, remetenteId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    private ConviteResp toResp(Convite c) {
        String eventoNome = eventoRepositorio.buscarPorId(new EventoId(c.getEventoId()))
                .map(Evento::getNome).orElse("Evento não encontrado");
        String remetenteNome = participanteRepositorio.buscarPorId(new ParticipanteId(c.getRemetenteId()))
                .map(p -> p.getNome().getValor()).orElse("Participante");
        String destinatarioNome = participanteRepositorio.buscarPorId(new ParticipanteId(c.getDestinatarioId()))
                .map(p -> p.getNome().getValor()).orElse("Participante");
        return new ConviteResp(
                c.getId().getValor().toString(),
                c.getEventoId().toString(),
                eventoNome,
                c.getRemetenteId().toString(),
                remetenteNome,
                c.getDestinatarioId().toString(),
                destinatarioNome,
                c.getStatusEfetivo().name(),
                c.getCriadoEm(),
                c.getExpiraEm());
    }
}
