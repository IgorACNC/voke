package br.voke.controle;

import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.notificacao.Notificacao;
import br.voke.dominio.evento.notificacao.NotificacaoId;
import br.voke.dominio.evento.notificacao.NotificacaoRepositorio;
import br.voke.dominio.evento.notificacao.NotificacaoServico;
import br.voke.dominio.evento.notificacao.SegmentacaoPorLote;
import br.voke.dominio.evento.notificacao.SegmentacaoTodos;
import br.voke.dominio.inscricao.inscricao.Inscricao;
import br.voke.dominio.inscricao.inscricao.InscricaoRepositorio;
import br.voke.dominio.inscricao.inscricao.InscricoesAtivasAgregado;
import br.voke.dominio.inscricao.inscricao.StatusInscricao;
import br.voke.seguranca.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    private final NotificacaoServico servico;
    private final NotificacaoRepositorio notificacaoRepositorio;
    private final EventoRepositorio eventoRepositorio;
    private final InscricaoRepositorio inscricaoRepositorio;
    private final JwtUtil jwtUtil;

    public NotificacaoController(NotificacaoServico servico,
                                  NotificacaoRepositorio notificacaoRepositorio,
                                  EventoRepositorio eventoRepositorio,
                                  InscricaoRepositorio inscricaoRepositorio,
                                  JwtUtil jwtUtil) {
        this.servico = servico;
        this.notificacaoRepositorio = notificacaoRepositorio;
        this.eventoRepositorio = eventoRepositorio;
        this.inscricaoRepositorio = inscricaoRepositorio;
        this.jwtUtil = jwtUtil;
    }

    record EnviarReq(UUID eventoId, String conteudo, Integer numeroLote) {}
    record AgendarReq(UUID eventoId, String conteudo, LocalDateTime dataAgendamento) {}
    record EditarReq(String conteudo) {}
    record NotificacaoResp(String id, String eventoId, String conteudo, String status,
                           LocalDateTime dataEnvio, LocalDateTime dataAgendamento,
                           int contadorEdicoes, String criterioSegmentacao,
                           int totalDestinatarios) {}
    record ErroResp(String mensagem) {}

    /**
     * Envio imediato. Se `numeroLote` for informado, aplica Strategy de Segmentação por Lote.
     * Sempre usa Iterator (RN1 - só inscritos ativos).
     */
    @PostMapping
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> enviar(@RequestBody EnviarReq req, HttpServletRequest httpReq) {
        try {
            Evento evento = exigirEventoDoOrganizadorLogado(req.eventoId(), httpReq);
            InscricoesAtivasAgregado elegiveis = montarAgregado(req.eventoId());

            Notificacao notif;
            if (req.numeroLote() != null) {
                Set<UUID> inscritosDoLote = inscritosNoLote(req.eventoId(), req.numeroLote());
                notif = servico.enviarSegmentada(req.eventoId(), req.conteudo(), evento.estaAtivo(),
                        elegiveis, new SegmentacaoPorLote(req.numeroLote(), inscritosDoLote));
            } else {
                // Iterator + Strategy "Todos" — equivalente ao envio simples mas explícito sobre o padrão
                notif = servico.enviarSegmentada(req.eventoId(), req.conteudo(), evento.estaAtivo(),
                        elegiveis, new SegmentacaoTodos());
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(toResposta(notif));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PostMapping("/agendar")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> agendar(@RequestBody AgendarReq req, HttpServletRequest httpReq) {
        try {
            Evento evento = exigirEventoDoOrganizadorLogado(req.eventoId(), httpReq);
            Notificacao notif = servico.agendar(req.eventoId(), req.conteudo(),
                    evento.estaAtivo(), req.dataAgendamento());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResposta(notif));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> cancelar(@PathVariable UUID id, HttpServletRequest httpReq) {
        try {
            exigirNotificacaoDoOrganizadorLogado(id, httpReq);
            servico.cancelarAgendada(new NotificacaoId(id));
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> editar(@PathVariable UUID id, @RequestBody EditarReq req,
                                    HttpServletRequest httpReq) {
        try {
            exigirNotificacaoDoOrganizadorLogado(id, httpReq);
            servico.editar(new NotificacaoId(id), req.conteudo());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> remover(@PathVariable UUID id, HttpServletRequest httpReq) {
        try {
            exigirNotificacaoDoOrganizadorLogado(id, httpReq);
            servico.remover(new NotificacaoId(id));
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @GetMapping("/evento/{eventoId}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> listarPorEvento(@PathVariable UUID eventoId, HttpServletRequest httpReq) {
        exigirEventoDoOrganizadorLogado(eventoId, httpReq);
        List<NotificacaoResp> resp = notificacaoRepositorio.buscarPorEventoId(eventoId).stream()
                .map(this::toResposta)
                .toList();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/minhas")
    @PreAuthorize("hasRole('PARTICIPANTE')")
    public ResponseEntity<?> minhasNotificacoes(HttpServletRequest httpReq) {
        UUID participanteId = idAutenticado(httpReq);
        List<NotificacaoResp> resp = servico.listarPorParticipante(participanteId).stream()
                // RN1: garante que só recebem participantes presentes no Set de destinatários
                .filter(n -> n.foiEnviadaPara(participanteId) || n.getDestinatariosIds().isEmpty())
                .map(this::toResposta)
                .toList();
        return ResponseEntity.ok(resp);
    }

    /* ---------- helpers ---------- */

    private InscricoesAtivasAgregado montarAgregado(UUID eventoId) {
        List<Inscricao> inscricoes = inscricaoRepositorio.buscarPorEventoId(eventoId);
        return new InscricoesAtivasAgregado(inscricoes);
    }

    private Set<UUID> inscritosNoLote(UUID eventoId, int numeroLote) {
        Evento evento = eventoRepositorio.buscarPorId(new EventoId(eventoId))
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
        if (evento.getLoteAtual() == null || evento.getLoteAtual().getNumero() != numeroLote) {
            return Set.of();
        }
        return inscricaoRepositorio.buscarPorEventoId(eventoId).stream()
                .filter(i -> i.getStatus() == StatusInscricao.CONFIRMADA)
                .map(Inscricao::getParticipanteId)
                .collect(Collectors.toSet());
    }

    private Evento exigirEventoDoOrganizadorLogado(UUID eventoId, HttpServletRequest httpReq) {
        Evento evento = eventoRepositorio.buscarPorId(new EventoId(eventoId))
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
        UUID organizadorLogado = idAutenticado(httpReq);
        if (!evento.getOrganizadorId().equals(organizadorLogado)) {
            throw new IllegalArgumentException("Você não tem permissão para esse evento");
        }
        return evento;
    }

    private void exigirNotificacaoDoOrganizadorLogado(UUID notificacaoId, HttpServletRequest httpReq) {
        Notificacao notif = notificacaoRepositorio.buscarPorId(new NotificacaoId(notificacaoId))
                .orElseThrow(() -> new IllegalArgumentException("Notificação não encontrada"));
        exigirEventoDoOrganizadorLogado(notif.getEventoId(), httpReq);
    }

    private UUID idAutenticado(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String idStr = jwtUtil.extrairClaims(token).get("id", String.class);
        return UUID.fromString(idStr);
    }

    private NotificacaoResp toResposta(Notificacao n) {
        return new NotificacaoResp(
                n.getId().getValor().toString(),
                n.getEventoId().toString(),
                n.getConteudo(),
                n.getStatus().name(),
                n.getDataEnvio(),
                n.getDataAgendamento(),
                n.getContadorEdicoes(),
                n.getCriterioSegmentacao(),
                n.getDestinatariosIds().size()
        );
    }
}
