package br.voke.controle;

import br.voke.aplicacao.evento.CriarGrupoEventoCasoDeUso;
import br.voke.aplicacao.evento.EnviarMensagemCanalCasoDeUso;
import br.voke.aplicacao.evento.ListarMensagensCanalCasoDeUso;
import br.voke.dominio.evento.chat.MensagemCanal;
import br.voke.dominio.evento.chat.TipoCanalChat;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.grupo.GrupoEvento;
import br.voke.dominio.evento.grupo.GrupoEventoId;
import br.voke.dominio.evento.grupo.GrupoEventoRepositorio;
import br.voke.dominio.evento.grupo.GrupoEventoServicoInterface;
import br.voke.dominio.inscricao.inscricao.InscricaoRepositorio;
import br.voke.dominio.pessoa.organizador.OrganizadorId;
import br.voke.dominio.pessoa.organizador.OrganizadorRepositorio;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;
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

@RestController
@RequestMapping("/api/grupos")
public class GrupoEventoController {

    private final CriarGrupoEventoCasoDeUso criarGrupo;
    private final GrupoEventoServicoInterface grupoServico;
    private final GrupoEventoRepositorio grupoRepositorio;
    private final EventoRepositorio eventoRepositorio;
    private final InscricaoRepositorio inscricaoRepositorio;
    private final ParticipanteRepositorio participanteRepositorio;
    private final OrganizadorRepositorio organizadorRepositorio;
    private final EnviarMensagemCanalCasoDeUso enviarMensagemCanal;
    private final ListarMensagensCanalCasoDeUso listarMensagensCanal;
    private final JwtUtil jwtUtil;

    public GrupoEventoController(CriarGrupoEventoCasoDeUso criarGrupo,
                                  GrupoEventoServicoInterface grupoServico,
                                  GrupoEventoRepositorio grupoRepositorio,
                                  EventoRepositorio eventoRepositorio,
                                  InscricaoRepositorio inscricaoRepositorio,
                                  ParticipanteRepositorio participanteRepositorio,
                                  OrganizadorRepositorio organizadorRepositorio,
                                  EnviarMensagemCanalCasoDeUso enviarMensagemCanal,
                                  ListarMensagensCanalCasoDeUso listarMensagensCanal,
                                  JwtUtil jwtUtil) {
        this.criarGrupo = criarGrupo;
        this.grupoServico = grupoServico;
        this.grupoRepositorio = grupoRepositorio;
        this.eventoRepositorio = eventoRepositorio;
        this.inscricaoRepositorio = inscricaoRepositorio;
        this.participanteRepositorio = participanteRepositorio;
        this.organizadorRepositorio = organizadorRepositorio;
        this.enviarMensagemCanal = enviarMensagemCanal;
        this.listarMensagensCanal = listarMensagensCanal;
        this.jwtUtil = jwtUtil;
    }

    record CriarGrupoReq(UUID eventoId, String nome, String regras) {}
    record EditarRegrasReq(String regras) {}
    record EnviarMensagemReq(String conteudo) {}
    record MensagemCanalResp(String id, String remetenteId, String remetenteNome,
                             String canalTipo, String canalId, String conteudo,
                             LocalDateTime enviadaEm) {}

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> criar(@RequestBody CriarGrupoReq req, HttpServletRequest httpReq) {
        UUID organizadorId = idAutenticado(httpReq);
        var evento = eventoRepositorio.buscarPorId(new EventoId(req.eventoId()))
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
        if (!evento.getOrganizadorId().equals(organizadorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErroResp("Acesso negado"));
        }
        GrupoEvento grupo = criarGrupo.executar(req.nome(), req.regras(), req.eventoId(),
                evento.getOrganizadorId(), organizadorId);
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

    // ======================== Chat do Grupo ========================

    @GetMapping("/{id}/mensagens")
    public ResponseEntity<?> listarMensagens(@PathVariable UUID id, HttpServletRequest httpReq) {
        UUID solicitanteId = idAutenticado(httpReq);
        GrupoEvento grupo = grupoRepositorio.buscarPorId(new GrupoEventoId(id))
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));
        boolean podeAcessar = podeAcessarChatGrupo(grupo, solicitanteId);
        List<MensagemCanal> mensagens = listarMensagensCanal.executar(
                TipoCanalChat.GRUPO_EVENTO, id, solicitanteId, podeAcessar);
        return ResponseEntity.ok(mensagens.stream().map(this::toMensagemResp).toList());
    }

    @PostMapping("/{id}/mensagens")
    public ResponseEntity<?> enviarMensagem(@PathVariable UUID id,
                                             @RequestBody EnviarMensagemReq req,
                                             HttpServletRequest httpReq) {
        UUID remetenteId = idAutenticado(httpReq);
        GrupoEvento grupo = grupoRepositorio.buscarPorId(new GrupoEventoId(id))
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));
        boolean podeAcessar = podeAcessarChatGrupo(grupo, remetenteId);
        MensagemCanal mensagem = enviarMensagemCanal.executar(
                TipoCanalChat.GRUPO_EVENTO, id, remetenteId, req.conteudo(), podeAcessar);
        return ResponseEntity.status(HttpStatus.CREATED).body(toMensagemResp(mensagem));
    }

    private boolean podeAcessarChatGrupo(GrupoEvento grupo, UUID solicitanteId) {
        boolean ehMembro = grupo.getMembrosIds().contains(solicitanteId);
        boolean ehOrganizador = grupo.getOrganizadorId().equals(solicitanteId);
        return ehMembro || ehOrganizador;
    }

    private MensagemCanalResp toMensagemResp(MensagemCanal m) {
        return new MensagemCanalResp(
                m.getId().getValor().toString(),
                m.getRemetenteId().toString(),
                resolverNomeRemetente(m.getRemetenteId()),
                m.getCanalTipo().name(),
                m.getCanalId().toString(),
                m.getConteudo(),
                m.getEnviadaEm());
    }

    private String resolverNomeRemetente(UUID remetenteId) {
        return participanteRepositorio.buscarPorId(new ParticipanteId(remetenteId))
                .map(p -> p.getNome().getValor())
                .orElseGet(() -> organizadorRepositorio.buscarPorId(new OrganizadorId(remetenteId))
                        .map(o -> o.getNome().getValor())
                        .orElse("Usuario"));
    }

    // ======================== Helpers ========================

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
