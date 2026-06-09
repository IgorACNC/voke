package br.voke.controle;

import br.voke.aplicacao.evento.CriarGrupoEventoCasoDeUso;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.grupo.GrupoEvento;
import br.voke.dominio.evento.grupo.GrupoEventoId;
import br.voke.dominio.evento.grupo.GrupoEventoRepositorio;
import br.voke.dominio.evento.grupo.GrupoEventoServicoInterface;
import br.voke.dominio.inscricao.inscricao.InscricaoRepositorio;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;
import br.voke.seguranca.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/grupos")
public class GrupoEventoController {

    private final CriarGrupoEventoCasoDeUso criarGrupo;
    private final GrupoEventoServicoInterface grupoServico;
    private final GrupoEventoRepositorio grupoRepositorio;
    private final EventoRepositorio eventoRepositorio;
    private final InscricaoRepositorio inscricaoRepositorio;
    private final ParticipanteRepositorio participanteRepositorio;
    private final JwtUtil jwtUtil;

    public GrupoEventoController(CriarGrupoEventoCasoDeUso criarGrupo,
                                  GrupoEventoServicoInterface grupoServico,
                                  GrupoEventoRepositorio grupoRepositorio,
                                  EventoRepositorio eventoRepositorio,
                                  InscricaoRepositorio inscricaoRepositorio,
                                  ParticipanteRepositorio participanteRepositorio,
                                  JwtUtil jwtUtil) {
        this.criarGrupo = criarGrupo;
        this.grupoServico = grupoServico;
        this.grupoRepositorio = grupoRepositorio;
        this.eventoRepositorio = eventoRepositorio;
        this.inscricaoRepositorio = inscricaoRepositorio;
        this.participanteRepositorio = participanteRepositorio;
        this.jwtUtil = jwtUtil;
    }

    record CriarGrupoReq(UUID eventoId, String nome, String regras) {}
    record EditarRegrasReq(String regras) {}

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> criar(@RequestBody CriarGrupoReq req, HttpServletRequest httpReq) {
        UUID organizadorId = idAutenticado(httpReq);
        var evento = eventoRepositorio.buscarPorId(new EventoId(req.eventoId()))
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
        if (!evento.getOrganizadorId().equals(organizadorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErroResp("Acesso negado"));
        }
        GrupoEvento grupo = criarGrupo.executar(req.nome(), req.regras(), req.eventoId(), organizadorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResposta(grupo));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<?> buscarPorEvento(@PathVariable UUID eventoId) {
        return grupoRepositorio.buscarPorEventoId(eventoId)
                .map(g -> ResponseEntity.ok(toResposta(g)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/membros")
    @PreAuthorize("hasRole('PARTICIPANTE')")
    public ResponseEntity<?> entrar(@PathVariable UUID id, HttpServletRequest httpReq) {
        UUID participanteId = idAutenticado(httpReq);
        GrupoEvento grupo = grupoRepositorio.buscarPorId(new GrupoEventoId(id))
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));

        boolean possuiInscricao = inscricaoRepositorio
                .contarPorParticipanteEEvento(participanteId, grupo.getEventoId()) > 0;

        int idade = participanteRepositorio.buscarPorId(new ParticipanteId(participanteId))
                .map(p -> p.getIdade())
                .orElseThrow(() -> new IllegalArgumentException("Participante não encontrado"));

        grupoServico.adicionarMembro(new GrupoEventoId(id), participanteId, possuiInscricao, idade);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/membros/{participanteId}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> removerMembro(@PathVariable UUID id, @PathVariable UUID participanteId,
                                            HttpServletRequest httpReq) {
        UUID organizadorId = idAutenticado(httpReq);
        GrupoEvento grupo = grupoRepositorio.buscarPorId(new GrupoEventoId(id))
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));
        if (!grupo.getOrganizadorId().equals(organizadorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErroResp("Acesso negado"));
        }
        grupo.removerMembro(participanteId);
        grupoRepositorio.salvar(grupo);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/regras")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> editarRegras(@PathVariable UUID id, @RequestBody EditarRegrasReq req,
                                           HttpServletRequest httpReq) {
        UUID organizadorId = idAutenticado(httpReq);
        grupoServico.editarRegras(new GrupoEventoId(id), req.regras(), organizadorId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> excluir(@PathVariable UUID id, HttpServletRequest httpReq) {
        UUID organizadorId = idAutenticado(httpReq);
        grupoServico.remover(new GrupoEventoId(id), organizadorId);
        return ResponseEntity.noContent().build();
    }

    private UUID idAutenticado(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String idStr = jwtUtil.extrairClaims(token).get("id", String.class);
        return UUID.fromString(idStr);
    }

    private record GrupoResp(String id, String nome, String regras, String eventoId,
                              String organizadorId, Set<UUID> membrosIds) {}

    private GrupoResp toResposta(GrupoEvento g) {
        return new GrupoResp(g.getId().getValor().toString(), g.getNome(), g.getRegras(),
                g.getEventoId().toString(), g.getOrganizadorId().toString(), g.getMembrosIds());
    }

    record ErroResp(String mensagem) {}
}
