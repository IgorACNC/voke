package br.voke.controle;

import br.voke.aplicacao.evento.AprovarSolicitacaoSubgrupoCasoDeUso;
import br.voke.aplicacao.evento.RejeitarSolicitacaoSubgrupoCasoDeUso;
import br.voke.aplicacao.evento.SolicitarEntradaSubgrupoCasoDeUso;
import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.grupo.GrupoEvento;
import br.voke.dominio.evento.grupo.GrupoEventoId;
import br.voke.dominio.evento.grupo.GrupoEventoRepositorio;
import br.voke.dominio.evento.subgrupo.Subgrupo;
import br.voke.dominio.evento.subgrupo.SubgrupoId;
import br.voke.dominio.evento.subgrupo.SubgrupoRepositorio;
import br.voke.dominio.evento.subgrupo.solicitacao.SolicitacaoSubgrupo;
import br.voke.dominio.evento.subgrupo.solicitacao.SolicitacaoSubgrupoId;
import br.voke.dominio.evento.subgrupo.solicitacao.SolicitacaoSubgrupoRepositorio;
import br.voke.seguranca.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subgrupos")
public class SolicitacaoSubgrupoController {

    private final SolicitarEntradaSubgrupoCasoDeUso solicitar;
    private final AprovarSolicitacaoSubgrupoCasoDeUso aprovar;
    private final RejeitarSolicitacaoSubgrupoCasoDeUso rejeitar;
    private final SolicitacaoSubgrupoRepositorio solicitacaoRepositorio;
    private final SubgrupoRepositorio subgrupoRepositorio;
    private final GrupoEventoRepositorio grupoEventoRepositorio;
    private final EventoRepositorio eventoRepositorio;
    private final JwtUtil jwtUtil;

    public SolicitacaoSubgrupoController(SolicitarEntradaSubgrupoCasoDeUso solicitar,
                                          AprovarSolicitacaoSubgrupoCasoDeUso aprovar,
                                          RejeitarSolicitacaoSubgrupoCasoDeUso rejeitar,
                                          SolicitacaoSubgrupoRepositorio solicitacaoRepositorio,
                                          SubgrupoRepositorio subgrupoRepositorio,
                                          GrupoEventoRepositorio grupoEventoRepositorio,
                                          EventoRepositorio eventoRepositorio,
                                          JwtUtil jwtUtil) {
        this.solicitar = solicitar;
        this.aprovar = aprovar;
        this.rejeitar = rejeitar;
        this.solicitacaoRepositorio = solicitacaoRepositorio;
        this.subgrupoRepositorio = subgrupoRepositorio;
        this.grupoEventoRepositorio = grupoEventoRepositorio;
        this.eventoRepositorio = eventoRepositorio;
        this.jwtUtil = jwtUtil;
    }

    record SolicitarReq(String mensagem) {}

    @PostMapping("/{subgrupoId}/solicitacoes")
    @PreAuthorize("hasRole('PARTICIPANTE')")
    public ResponseEntity<?> solicitar(@PathVariable UUID subgrupoId,
                                        @RequestBody SolicitarReq req,
                                        HttpServletRequest httpReq) {
        UUID participanteId = idAutenticado(httpReq);
        Subgrupo sub = buscarSubgrupo(subgrupoId);
        boolean ehMembroGrupoPrincipal = ehMembroGrupoPrincipal(sub.getGrupoEventoId(), participanteId);
        SolicitacaoSubgrupo s = solicitar.executar(subgrupoId, participanteId,
                req.mensagem(), ehMembroGrupoPrincipal);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResposta(s));
    }

    @GetMapping("/{subgrupoId}/solicitacoes")
    public ResponseEntity<?> listarPorSubgrupo(@PathVariable UUID subgrupoId,
                                                HttpServletRequest httpReq) {
        UUID solicitanteId = idAutenticado(httpReq);
        Subgrupo sub = buscarSubgrupo(subgrupoId);
        boolean ehGestor = ehGestor(sub, solicitanteId);
        List<SolicitacaoSubgrupo> todas = solicitacaoRepositorio.buscarPorSubgrupo(subgrupoId);
        List<SolicitacaoSubgrupo> visiveis = ehGestor ? todas
                : todas.stream().filter(s -> s.getParticipanteId().equals(solicitanteId)).toList();
        return ResponseEntity.ok(visiveis.stream().map(this::toResposta).toList());
    }

    @GetMapping("/solicitacoes/minhas")
    @PreAuthorize("hasRole('PARTICIPANTE')")
    public ResponseEntity<?> minhas(HttpServletRequest httpReq) {
        UUID participanteId = idAutenticado(httpReq);
        List<SolicitacaoSubgrupo> minhas = solicitacaoRepositorio.buscarPorParticipante(participanteId);
        return ResponseEntity.ok(minhas.stream().map(this::toResposta).toList());
    }

    @PutMapping("/solicitacoes/{id}/aprovar")
    public ResponseEntity<?> aprovar(@PathVariable UUID id, HttpServletRequest httpReq) {
        UUID solicitanteId = idAutenticado(httpReq);
        SolicitacaoSubgrupo solicitacao = solicitacaoRepositorio
                .buscarPorId(new SolicitacaoSubgrupoId(id))
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));
        Subgrupo sub = buscarSubgrupo(solicitacao.getSubgrupoId());
        boolean ehGestor = ehGestor(sub, solicitanteId);
        SolicitacaoSubgrupo aprovada = aprovar.executar(id, solicitanteId, ehGestor);
        return ResponseEntity.ok(toResposta(aprovada));
    }

    @PutMapping("/solicitacoes/{id}/rejeitar")
    public ResponseEntity<?> rejeitar(@PathVariable UUID id, HttpServletRequest httpReq) {
        UUID solicitanteId = idAutenticado(httpReq);
        SolicitacaoSubgrupo solicitacao = solicitacaoRepositorio
                .buscarPorId(new SolicitacaoSubgrupoId(id))
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));
        Subgrupo sub = buscarSubgrupo(solicitacao.getSubgrupoId());
        boolean ehGestor = ehGestor(sub, solicitanteId);
        SolicitacaoSubgrupo rejeitada = rejeitar.executar(id, solicitanteId, ehGestor);
        return ResponseEntity.ok(toResposta(rejeitada));
    }

    private UUID idAutenticado(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String idStr = jwtUtil.extrairClaims(token).get("id", String.class);
        return UUID.fromString(idStr);
    }

    private Subgrupo buscarSubgrupo(UUID id) {
        return subgrupoRepositorio.buscarPorId(new SubgrupoId(id))
                .orElseThrow(() -> new IllegalArgumentException("Subgrupo não encontrado"));
    }

    private boolean ehMembroGrupoPrincipal(UUID grupoId, UUID participanteId) {
        return grupoEventoRepositorio.buscarPorId(new GrupoEventoId(grupoId))
                .map(g -> g.getMembrosIds().contains(participanteId))
                .orElse(false);
    }

    private boolean ehGestor(Subgrupo sub, UUID solicitanteId) {
        GrupoEvento grupo = grupoEventoRepositorio.buscarPorId(new GrupoEventoId(sub.getGrupoEventoId()))
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));
        Evento evento = eventoRepositorio.buscarPorId(new EventoId(grupo.getEventoId()))
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
        boolean ehOrganizador = evento.getOrganizadorId().equals(solicitanteId);
        boolean ehModerador = sub.ehModerador(solicitanteId);
        return ehOrganizador || ehModerador;
    }

    private SolicitacaoResp toResposta(SolicitacaoSubgrupo s) {
        return new SolicitacaoResp(
                s.getId().getValor().toString(),
                s.getSubgrupoId().toString(),
                s.getParticipanteId().toString(),
                s.getMensagem(),
                s.getStatus().name(),
                s.getDataSolicitacao(),
                s.getDataDecisao(),
                s.getDecididoPor() != null ? s.getDecididoPor().toString() : null);
    }

    record SolicitacaoResp(String id, String subgrupoId, String participanteId, String mensagem,
                            String status, LocalDateTime dataSolicitacao,
                            LocalDateTime dataDecisao, String decididoPor) {}
}
