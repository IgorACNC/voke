package br.voke.controle;

import br.voke.aplicacao.evento.AdicionarFavoritoCasoDeUso;
import br.voke.aplicacao.evento.ListarFavoritosDoParticipanteCasoDeUso;
import br.voke.aplicacao.evento.RemoverFavoritoCasoDeUso;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.favorito.Favorito;
import br.voke.seguranca.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/favoritos")
@PreAuthorize("hasRole('PARTICIPANTE')")
public class FavoritoController {

    private final AdicionarFavoritoCasoDeUso adicionar;
    private final RemoverFavoritoCasoDeUso remover;
    private final ListarFavoritosDoParticipanteCasoDeUso listar;
    private final EventoRepositorio eventoRepositorio;
    private final JwtUtil jwtUtil;

    public FavoritoController(AdicionarFavoritoCasoDeUso adicionar,
                               RemoverFavoritoCasoDeUso remover,
                               ListarFavoritosDoParticipanteCasoDeUso listar,
                               EventoRepositorio eventoRepositorio,
                               JwtUtil jwtUtil) {
        this.adicionar = adicionar;
        this.remover = remover;
        this.listar = listar;
        this.eventoRepositorio = eventoRepositorio;
        this.jwtUtil = jwtUtil;
    }

    record FavoritoResp(String id, String eventoId) {}

    @PostMapping("/{eventoId}")
    public ResponseEntity<?> adicionar(@PathVariable UUID eventoId, HttpServletRequest req) {
        UUID participanteId = idAutenticado(req);
        String status = eventoRepositorio.buscarPorId(new EventoId(eventoId))
                .map(e -> e.getStatus().name())
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
        Favorito favorito = adicionar.executar(participanteId, eventoId, status);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new FavoritoResp(favorito.getId().getValor().toString(), eventoId.toString()));
    }

    @DeleteMapping("/{favoritoId}")
    public ResponseEntity<?> remover(@PathVariable UUID favoritoId, HttpServletRequest req) {
        remover.executar(favoritoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> listar(HttpServletRequest req) {
        UUID participanteId = idAutenticado(req);
        List<Favorito> favoritos = listar.executar(participanteId);
        List<FavoritoResp> resposta = favoritos.stream()
                .map(f -> new FavoritoResp(f.getId().getValor().toString(), f.getEventoId().toString()))
                .toList();
        return ResponseEntity.ok(resposta);
    }

    private UUID idAutenticado(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String idStr = jwtUtil.extrairClaims(token).get("id", String.class);
        return UUID.fromString(idStr);
    }
}
