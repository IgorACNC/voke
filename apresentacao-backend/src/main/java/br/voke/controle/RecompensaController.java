package br.voke.controle;

import br.voke.aplicacao.fidelidade.*;
import br.voke.dominio.fidelidade.recompensa.Recompensa;
import br.voke.dominio.fidelidade.pontos.ContaPontosRepositorio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recompensas")
public class RecompensaController {

    private final CadastrarRecompensaCasoDeUso cadastrar;
    private final EditarRecompensaCasoDeUso editar;
    private final RemoverRecompensaCasoDeUso remover;
    private final InativarRecompensaCasoDeUso inativar;
    private final ListarRecompensasOrganizadorCasoDeUso listar;
    private final ListarRecompensasAtivasCasoDeUso listarAtivas;
    private final ResgatarRecompensaCasoDeUso resgatar;
    private final ConsultarSaldoPontosCasoDeUso consultarPontos;

    public RecompensaController(CadastrarRecompensaCasoDeUso cadastrar,
                                 EditarRecompensaCasoDeUso editar,
                                 RemoverRecompensaCasoDeUso remover,
                                 InativarRecompensaCasoDeUso inativar,
                                 ListarRecompensasOrganizadorCasoDeUso listar,
                                 ListarRecompensasAtivasCasoDeUso listarAtivas,
                                 ResgatarRecompensaCasoDeUso resgatar,
                                 ConsultarSaldoPontosCasoDeUso consultarPontos) {
        this.cadastrar = cadastrar;
        this.editar = editar;
        this.remover = remover;
        this.inativar = inativar;
        this.listar = listar;
        this.listarAtivas = listarAtivas;
        this.resgatar = resgatar;
        this.consultarPontos = consultarPontos;
    }

    record CriarReq(String nome, String descricao, int custoEmPontos, int estoqueTotal, UUID organizadorId) {}
    record EditarReq(String novaDescricao, Integer novoCusto) {}

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> criar(@RequestBody CriarReq req) {
        try {
            Recompensa r = cadastrar.executar(req.nome(), req.descricao(), req.custoEmPontos(),
                    req.estoqueTotal(), req.organizadorId());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResposta(r));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> editar(@PathVariable UUID id, @RequestBody EditarReq req) {
        try {
            if (req.novaDescricao() != null) editar.executarAtualizarDescricao(id, req.novaDescricao());
            if (req.novoCusto() != null) editar.executarAlterarCusto(id, req.novoCusto());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> remover(@PathVariable UUID id) {
        try {
            remover.executar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/inativar")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> inativar(@PathVariable UUID id) {
        try {
            inativar.executar(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @GetMapping("/organizador/{organizadorId}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<List<RecompensaResp>> listar(@PathVariable UUID organizadorId) {
        return ResponseEntity.ok(listar.executar(organizadorId).stream().map(this::toResposta).toList());
    }

    @GetMapping("/ativas")
    @PreAuthorize("hasRole('PARTICIPANTE')")
    public ResponseEntity<List<RecompensaResp>> listarAtivas() {
        return ResponseEntity.ok(listarAtivas.executar().stream().map(this::toResposta).toList());
    }

    @GetMapping("/participante/{participanteId}/saldo-pontos")
    @PreAuthorize("hasRole('PARTICIPANTE')")
    public ResponseEntity<?> saldoPontos(@PathVariable UUID participanteId) {
        return ResponseEntity.ok(new SaldoResp(consultarPontos.executar(participanteId)));
    }

    @PostMapping("/{id}/resgatar")
    @PreAuthorize("hasRole('PARTICIPANTE')")
    public ResponseEntity<?> resgatar(@PathVariable UUID id, @RequestParam UUID participanteId) {
        try {
            resgatar.executar(participanteId, id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    private record RecompensaResp(String id, String nome, String descricao,
                                   int custoEmPontos, int estoqueDisponivel, int estoqueTotal, boolean ativa) {}

    private record SaldoResp(int saldo) {}

    private RecompensaResp toResposta(Recompensa r) {
        return new RecompensaResp(r.getId().getValor().toString(), r.getNome(), r.getDescricao(),
                r.getCustoEmPontos(), r.getEstoqueRestante(), r.getEstoqueTotal(), r.isAtiva());
    }

    record ErroResp(String mensagem) {}
}
