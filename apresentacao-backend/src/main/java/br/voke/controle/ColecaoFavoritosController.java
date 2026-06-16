package br.voke.controle;

import br.voke.aplicacao.evento.*;
import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.favorito.ColecaoFavoritos;
import br.voke.dominio.evento.favorito.VisibilidadeColecao;
import br.voke.seguranca.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/colecoes-favoritos")
@PreAuthorize("hasRole('PARTICIPANTE')")
public class ColecaoFavoritosController {

    private final CriarColecaoFavoritosCasoDeUso criar;
    private final EditarColecaoFavoritosCasoDeUso editar;
    private final ExcluirColecaoFavoritosCasoDeUso excluir;
    private final ListarColecoesDoParticipanteCasoDeUso listar;
    private final BuscarColecaoCasoDeUso buscar;
    private final AdicionarEventoColecaoCasoDeUso adicionarEvento;
    private final RemoverEventoColecaoCasoDeUso removerEvento;
    private final MoverEventoEntreColecoesCasoDeUso moverEvento;
    private final ReordenarEventoColecaoCasoDeUso reordenar;
    private final DuplicarColecaoCasoDeUso duplicar;
    private final EventoRepositorio eventoRepositorio;
    private final JwtUtil jwtUtil;

    public ColecaoFavoritosController(CriarColecaoFavoritosCasoDeUso criar,
                                       EditarColecaoFavoritosCasoDeUso editar,
                                       ExcluirColecaoFavoritosCasoDeUso excluir,
                                       ListarColecoesDoParticipanteCasoDeUso listar,
                                       BuscarColecaoCasoDeUso buscar,
                                       AdicionarEventoColecaoCasoDeUso adicionarEvento,
                                       RemoverEventoColecaoCasoDeUso removerEvento,
                                       MoverEventoEntreColecoesCasoDeUso moverEvento,
                                       ReordenarEventoColecaoCasoDeUso reordenar,
                                       DuplicarColecaoCasoDeUso duplicar,
                                       EventoRepositorio eventoRepositorio,
                                       JwtUtil jwtUtil) {
        this.criar = criar;
        this.editar = editar;
        this.excluir = excluir;
        this.listar = listar;
        this.buscar = buscar;
        this.adicionarEvento = adicionarEvento;
        this.removerEvento = removerEvento;
        this.moverEvento = moverEvento;
        this.reordenar = reordenar;
        this.duplicar = duplicar;
        this.eventoRepositorio = eventoRepositorio;
        this.jwtUtil = jwtUtil;
    }

    record CriarReq(String nome, String visibilidade) {}
    record EditarReq(String nome, String visibilidade) {}
    record AdicionarEventoReq(String eventoId) {}
    record MoverEventoReq(String destinoId, String eventoId) {}
    record ReordenarReq(int novaOrdem) {}

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody CriarReq req, HttpServletRequest httpReq) {
        UUID participanteId = idAutenticado(httpReq);
        VisibilidadeColecao vis = VisibilidadeColecao.valueOf(req.visibilidade().toUpperCase());
        ColecaoFavoritos colecao = criar.executar(participanteId, req.nome(), vis);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResumo(colecao));
    }

    @GetMapping
    public ResponseEntity<?> listar(HttpServletRequest httpReq) {
        UUID participanteId = idAutenticado(httpReq);
        List<ColecaoFavoritos> colecoes = listar.executar(participanteId);
        return ResponseEntity.ok(colecoes.stream().map(this::toResumo).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalhe(@PathVariable UUID id) {
        return ResponseEntity.ok(toDetalhe(buscar.executar(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable UUID id, @RequestBody EditarReq req,
                                     HttpServletRequest httpReq) {
        VisibilidadeColecao vis = VisibilidadeColecao.valueOf(req.visibilidade().toUpperCase());
        ColecaoFavoritos colecao = editar.executar(id, req.nome(), vis);
        return ResponseEntity.ok(toResumo(colecao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable UUID id, HttpServletRequest httpReq) {
        excluir.executar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/eventos")
    public ResponseEntity<?> adicionarEvento(@PathVariable UUID id,
                                              @RequestBody AdicionarEventoReq req) {
        UUID eventoId = UUID.fromString(req.eventoId());
        ColecaoFavoritos colecao = adicionarEvento.executar(id, eventoId);
        return ResponseEntity.ok(toDetalhe(colecao));
    }

    @DeleteMapping("/{id}/eventos/{eventoId}")
    public ResponseEntity<?> removerEvento(@PathVariable UUID id, @PathVariable UUID eventoId) {
        ColecaoFavoritos colecao = removerEvento.executar(id, eventoId);
        return ResponseEntity.ok(toDetalhe(colecao));
    }

    @PostMapping("/{id}/mover")
    public ResponseEntity<?> moverEvento(@PathVariable UUID id, @RequestBody MoverEventoReq req) {
        UUID destinoId = UUID.fromString(req.destinoId());
        UUID eventoId = UUID.fromString(req.eventoId());
        ColecaoFavoritos destino = moverEvento.executar(id, destinoId, eventoId);
        return ResponseEntity.ok(toDetalhe(destino));
    }

    @PostMapping("/{id}/reordenar/{eventoId}")
    public ResponseEntity<?> reordenar(@PathVariable UUID id, @PathVariable UUID eventoId,
                                        @RequestBody ReordenarReq req) {
        ColecaoFavoritos colecao = reordenar.executar(id, eventoId, req.novaOrdem());
        return ResponseEntity.ok(toDetalhe(colecao));
    }

    @PostMapping("/{id}/duplicar")
    public ResponseEntity<?> duplicar(@PathVariable UUID id, HttpServletRequest httpReq) {
        UUID participanteId = idAutenticado(httpReq);
        ColecaoFavoritos copia = duplicar.executar(id, participanteId);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResumo(copia));
    }

    private record ItemResp(String eventoId, String nomeEvento, String local,
                             LocalDateTime dataHoraInicio, int ordem) {}

    private record ColecaoResumoResp(String id, String nome, String visibilidade,
                                      LocalDateTime dataCriacao, int quantidadeItens) {}

    private record ColecaoDetalheResp(String id, String nome, String visibilidade,
                                       LocalDateTime dataCriacao, List<ItemResp> itens) {}

    private ColecaoResumoResp toResumo(ColecaoFavoritos c) {
        return new ColecaoResumoResp(
                c.getId().getValor().toString(), c.getNome(),
                c.getVisibilidade().name(), c.getDataCriacao(), c.getQuantidadeItens());
    }

    private ColecaoDetalheResp toDetalhe(ColecaoFavoritos c) {
        List<ItemResp> itens = c.getItensOrdenados().stream()
                .map(i -> {
                    Optional<Evento> evento = eventoRepositorio.buscarPorId(new EventoId(i.getEventoId()));
                    String nome = evento.map(Evento::getNome).orElse("Evento removido");
                    String local = evento.map(Evento::getLocal).orElse("");
                    LocalDateTime data = evento.map(Evento::getDataHoraInicio).orElse(null);
                    return new ItemResp(i.getEventoId().toString(), nome, local, data, i.getOrdem());
                })
                .toList();
        return new ColecaoDetalheResp(
                c.getId().getValor().toString(), c.getNome(),
                c.getVisibilidade().name(), c.getDataCriacao(), itens);
    }

    private UUID idAutenticado(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String idStr = jwtUtil.extrairClaims(token).get("id", String.class);
        return UUID.fromString(idStr);
    }
}
