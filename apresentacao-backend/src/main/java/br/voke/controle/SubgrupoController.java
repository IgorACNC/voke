package br.voke.controle;

import br.voke.aplicacao.evento.CriarSubgrupoCasoDeUso;
import br.voke.aplicacao.evento.EnviarMensagemCanalCasoDeUso;
import br.voke.aplicacao.evento.ListarMensagensCanalCasoDeUso;
import br.voke.dominio.evento.chat.MensagemCanal;
import br.voke.dominio.evento.chat.TipoCanalChat;
import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.grupo.GrupoEvento;
import br.voke.dominio.evento.grupo.GrupoEventoId;
import br.voke.dominio.evento.grupo.GrupoEventoRepositorio;
import br.voke.dominio.evento.subgrupo.CategoriaSubgrupo;
import br.voke.dominio.evento.subgrupo.Subgrupo;
import br.voke.dominio.evento.subgrupo.SubgrupoId;
import br.voke.dominio.evento.subgrupo.SubgrupoRepositorio;
import br.voke.dominio.evento.subgrupo.SubgrupoServicoInterface;
import br.voke.dominio.evento.subgrupo.TipoSubgrupo;
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
@RequestMapping("/api/subgrupos")
public class SubgrupoController {

    private final CriarSubgrupoCasoDeUso criarSubgrupo;
    private final SubgrupoServicoInterface subgrupoServico;
    private final SubgrupoRepositorio subgrupoRepositorio;
    private final GrupoEventoRepositorio grupoEventoRepositorio;
    private final EventoRepositorio eventoRepositorio;
    private final ParticipanteRepositorio participanteRepositorio;
    private final OrganizadorRepositorio organizadorRepositorio;
    private final EnviarMensagemCanalCasoDeUso enviarMensagemCanal;
    private final ListarMensagensCanalCasoDeUso listarMensagensCanal;
    private final JwtUtil jwtUtil;

    public SubgrupoController(CriarSubgrupoCasoDeUso criarSubgrupo,
                               SubgrupoServicoInterface subgrupoServico,
                               SubgrupoRepositorio subgrupoRepositorio,
                               GrupoEventoRepositorio grupoEventoRepositorio,
                               EventoRepositorio eventoRepositorio,
                               ParticipanteRepositorio participanteRepositorio,
                               OrganizadorRepositorio organizadorRepositorio,
                               EnviarMensagemCanalCasoDeUso enviarMensagemCanal,
                               ListarMensagensCanalCasoDeUso listarMensagensCanal,
                               JwtUtil jwtUtil) {
        this.criarSubgrupo = criarSubgrupo;
        this.subgrupoServico = subgrupoServico;
        this.subgrupoRepositorio = subgrupoRepositorio;
        this.grupoEventoRepositorio = grupoEventoRepositorio;
        this.eventoRepositorio = eventoRepositorio;
        this.participanteRepositorio = participanteRepositorio;
        this.organizadorRepositorio = organizadorRepositorio;
        this.enviarMensagemCanal = enviarMensagemCanal;
        this.listarMensagensCanal = listarMensagensCanal;
        this.jwtUtil = jwtUtil;
    }

    record CriarSubgrupoReq(UUID grupoEventoId, String nome, String descricao, String regras,
                            CategoriaSubgrupo categoria, TipoSubgrupo tipo, int limiteMembros) {}

    record EditarRegrasReq(String regras, String descricao) {}

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> criar(@RequestBody CriarSubgrupoReq req, HttpServletRequest httpReq) {
        UUID solicitanteId = idAutenticado(httpReq);
        GrupoEvento grupo = buscarGrupo(req.grupoEventoId());
        Evento evento = buscarEvento(grupo.getEventoId());
        boolean ehOrganizador = evento.getOrganizadorId().equals(solicitanteId);
        Subgrupo s = criarSubgrupo.executar(req.nome(), req.descricao(), req.regras(),
                req.grupoEventoId(), req.categoria(), req.tipo(), req.limiteMembros(),
                solicitanteId, ehOrganizador);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResposta(s));
    }

    @GetMapping("/grupo/{grupoEventoId}")
    public ResponseEntity<?> listarPorGrupo(@PathVariable UUID grupoEventoId) {
        List<Subgrupo> subgrupos = subgrupoRepositorio.buscarPorGrupoEventoId(grupoEventoId);
        return ResponseEntity.ok(subgrupos.stream().map(this::toResposta).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable UUID id) {
        return subgrupoRepositorio.buscarPorId(new SubgrupoId(id))
                .map(s -> ResponseEntity.ok(toResposta(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/membros")
    public ResponseEntity<?> entrar(@PathVariable UUID id, HttpServletRequest httpReq) {
        UUID participanteId = idAutenticado(httpReq);
        Subgrupo sub = buscarSubgrupo(id);
        boolean ehMembroGrupoPrincipal = ehMembroGrupoPrincipal(sub.getGrupoEventoId(), participanteId);
        boolean ehGestor = ehGestor(sub, participanteId);
        subgrupoServico.adicionarMembro(new SubgrupoId(id), participanteId, participanteId,
                ehMembroGrupoPrincipal, ehGestor);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/membros/{participanteId}")
    public ResponseEntity<?> adicionarMembro(@PathVariable UUID id,
                                              @PathVariable UUID participanteId,
                                              HttpServletRequest httpReq) {
        UUID solicitanteId = idAutenticado(httpReq);
        Subgrupo sub = buscarSubgrupo(id);
        boolean ehGestor = ehGestor(sub, solicitanteId);
        boolean ehMembroGrupoPrincipal = ehMembroGrupoPrincipal(sub.getGrupoEventoId(), participanteId);
        subgrupoServico.adicionarMembro(new SubgrupoId(id), participanteId, solicitanteId,
                ehMembroGrupoPrincipal, ehGestor);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/membros/{participanteId}")
    public ResponseEntity<?> removerMembro(@PathVariable UUID id,
                                            @PathVariable UUID participanteId,
                                            HttpServletRequest httpReq) {
        UUID solicitanteId = idAutenticado(httpReq);
        Subgrupo sub = buscarSubgrupo(id);
        boolean ehGestor = ehGestor(sub, solicitanteId);
        subgrupoServico.removerMembro(new SubgrupoId(id), participanteId, solicitanteId, ehGestor);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/regras")
    public ResponseEntity<?> editarRegras(@PathVariable UUID id,
                                           @RequestBody EditarRegrasReq req,
                                           HttpServletRequest httpReq) {
        UUID solicitanteId = idAutenticado(httpReq);
        Subgrupo sub = buscarSubgrupo(id);
        boolean ehGestor = ehGestor(sub, solicitanteId);
        subgrupoServico.editarRegras(new SubgrupoId(id), req.regras(), req.descricao(),
                solicitanteId, ehGestor);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> excluir(@PathVariable UUID id, HttpServletRequest httpReq) {
        UUID solicitanteId = idAutenticado(httpReq);
        Subgrupo sub = buscarSubgrupo(id);
        boolean ehOrganizador = ehOrganizadorDoEvento(sub.getGrupoEventoId(), solicitanteId);
        subgrupoServico.remover(new SubgrupoId(id), solicitanteId, ehOrganizador);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/moderador/{participanteId}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> promoverModerador(@PathVariable UUID id,
                                                @PathVariable UUID participanteId,
                                                HttpServletRequest httpReq) {
        UUID solicitanteId = idAutenticado(httpReq);
        Subgrupo sub = buscarSubgrupo(id);
        boolean ehOrganizador = ehOrganizadorDoEvento(sub.getGrupoEventoId(), solicitanteId);
        subgrupoServico.promoverModerador(new SubgrupoId(id), participanteId, solicitanteId,
                ehOrganizador);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/moderador")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> removerModerador(@PathVariable UUID id, HttpServletRequest httpReq) {
        UUID solicitanteId = idAutenticado(httpReq);
        Subgrupo sub = buscarSubgrupo(id);
        boolean ehOrganizador = ehOrganizadorDoEvento(sub.getGrupoEventoId(), solicitanteId);
        subgrupoServico.removerModerador(new SubgrupoId(id), solicitanteId, ehOrganizador);
        return ResponseEntity.noContent().build();
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

    private GrupoEvento buscarGrupo(UUID grupoId) {
        return grupoEventoRepositorio.buscarPorId(new GrupoEventoId(grupoId))
                .orElseThrow(() -> new IllegalArgumentException("Grupo de evento não encontrado"));
    }

    private Evento buscarEvento(UUID eventoId) {
        return eventoRepositorio.buscarPorId(new EventoId(eventoId))
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
    }

    private boolean ehMembroGrupoPrincipal(UUID grupoId, UUID participanteId) {
        return grupoEventoRepositorio.buscarPorId(new GrupoEventoId(grupoId))
                .map(g -> g.getMembrosIds().contains(participanteId))
                .orElse(false);
    }

    private boolean ehGestor(Subgrupo sub, UUID solicitanteId) {
        GrupoEvento grupo = buscarGrupo(sub.getGrupoEventoId());
        Evento evento = buscarEvento(grupo.getEventoId());
        boolean ehOrganizador = evento.getOrganizadorId().equals(solicitanteId);
        boolean ehModerador = sub.ehModerador(solicitanteId);
        return ehOrganizador || ehModerador;
    }

    private boolean ehOrganizadorDoEvento(UUID grupoId, UUID solicitanteId) {
        GrupoEvento grupo = buscarGrupo(grupoId);
        Evento evento = buscarEvento(grupo.getEventoId());
        return evento.getOrganizadorId().equals(solicitanteId);
    }

    private SubgrupoResp toResposta(Subgrupo s) {
        return new SubgrupoResp(
                s.getId().getValor().toString(),
                s.getNome(), s.getDescricao(), s.getRegras(),
                s.getGrupoEventoId().toString(),
                s.getCategoria().name(), s.getTipo().name(),
                s.getLimiteMembros(),
                s.getModeradorId() != null ? s.getModeradorId().toString() : null,
                s.getMembrosIds());
    }

    record SubgrupoResp(String id, String nome, String descricao, String regras,
                         String grupoEventoId, String categoria, String tipo,
                         int limiteMembros, String moderadorId, Set<UUID> membrosIds) {}

    // ======================== Chat do Subgrupo ========================

    record EnviarMensagemReq(String conteudo) {}
    record MensagemCanalResp(String id, String remetenteId, String remetenteNome,
                             String canalTipo, String canalId, String conteudo,
                             LocalDateTime enviadaEm) {}

    @GetMapping("/{id}/mensagens")
    public ResponseEntity<?> listarMensagens(@PathVariable UUID id, HttpServletRequest httpReq) {
        UUID solicitanteId = idAutenticado(httpReq);
        Subgrupo sub = buscarSubgrupo(id);
        boolean podeAcessar = podeAcessarChatSubgrupo(sub, solicitanteId);
        List<MensagemCanal> mensagens = listarMensagensCanal.executar(
                TipoCanalChat.SUBGRUPO, id, solicitanteId, podeAcessar);
        return ResponseEntity.ok(mensagens.stream().map(this::toMensagemResp).toList());
    }

    @PostMapping("/{id}/mensagens")
    public ResponseEntity<?> enviarMensagem(@PathVariable UUID id,
                                             @RequestBody EnviarMensagemReq req,
                                             HttpServletRequest httpReq) {
        UUID remetenteId = idAutenticado(httpReq);
        Subgrupo sub = buscarSubgrupo(id);
        boolean podeAcessar = podeAcessarChatSubgrupo(sub, remetenteId);
        MensagemCanal mensagem = enviarMensagemCanal.executar(
                TipoCanalChat.SUBGRUPO, id, remetenteId, req.conteudo(), podeAcessar);
        return ResponseEntity.status(HttpStatus.CREATED).body(toMensagemResp(mensagem));
    }

    private boolean podeAcessarChatSubgrupo(Subgrupo sub, UUID solicitanteId) {
        boolean ehMembro = sub.getMembrosIds().contains(solicitanteId);
        return ehMembro || ehGestor(sub, solicitanteId);
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

    record ErroResp(String mensagem) {}
}
