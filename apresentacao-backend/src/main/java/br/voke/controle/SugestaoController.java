package br.voke.controle;

import br.voke.aplicacao.fidelidade.AvaliarSugestaoCasoDeUso;
import br.voke.aplicacao.fidelidade.ConfigurarPreferenciasCasoDeUso;
import br.voke.aplicacao.fidelidade.GerarSugestoesSemanaisCasoDeUso;
import br.voke.aplicacao.fidelidade.ListarSugestoesParticipanteCasoDeUso;
import br.voke.aplicacao.fidelidade.RemoverSugestaoCasoDeUso;
import br.voke.dominio.fidelidade.sugestao.Sugestao;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/sugestoes")
@PreAuthorize("hasRole('PARTICIPANTE')")
public class SugestaoController {

    private final ConfigurarPreferenciasCasoDeUso configurarPreferencias;
    private final ListarSugestoesParticipanteCasoDeUso listarSugestoes;
    private final AvaliarSugestaoCasoDeUso avaliarSugestao;
    private final RemoverSugestaoCasoDeUso removerSugestao;
    private final GerarSugestoesSemanaisCasoDeUso gerarSugestoes;

    public SugestaoController(ConfigurarPreferenciasCasoDeUso configurarPreferencias,
                              ListarSugestoesParticipanteCasoDeUso listarSugestoes,
                              AvaliarSugestaoCasoDeUso avaliarSugestao,
                              RemoverSugestaoCasoDeUso removerSugestao,
                              GerarSugestoesSemanaisCasoDeUso gerarSugestoes) {
        this.configurarPreferencias = configurarPreferencias;
        this.listarSugestoes = listarSugestoes;
        this.avaliarSugestao = avaliarSugestao;
        this.removerSugestao = removerSugestao;
        this.gerarSugestoes = gerarSugestoes;
    }

    record PreferenciasReq(UUID participanteId, Set<String> categorias) {}
    record AvaliarReq(boolean aprovada) {}
    record SugestaoResp(UUID id, UUID eventoId, String status) {}
    record ErroResp(String mensagem) {}

    @PostMapping("/preferencias")
    public ResponseEntity<?> configurarPreferencias(@RequestBody PreferenciasReq req) {
        try {
            configurarPreferencias.executar(req.participanteId(), req.categorias());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @GetMapping("/participante/{participanteId}")
    public ResponseEntity<List<SugestaoResp>> listar(@PathVariable UUID participanteId) {
        List<SugestaoResp> resp = listarSugestoes.executar(participanteId).stream()
                .map(this::toResp)
                .toList();
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{id}/avaliar")
    public ResponseEntity<?> avaliar(@PathVariable UUID id, @RequestBody AvaliarReq req) {
        try {
            avaliarSugestao.executar(id, req.aprovada());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable UUID id) {
        try {
            removerSugestao.executar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PostMapping("/gerar/{participanteId}")
    public ResponseEntity<?> gerar(@PathVariable UUID participanteId) {
        try {
            List<SugestaoResp> resp = gerarSugestoes.executar(participanteId).stream()
                    .map(this::toResp)
                    .toList();
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    private SugestaoResp toResp(Sugestao s) {
        return new SugestaoResp(s.getId().getValor(), s.getEventoId(), s.getStatus().name());
    }
}
