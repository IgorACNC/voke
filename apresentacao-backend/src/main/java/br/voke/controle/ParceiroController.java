package br.voke.controle;

import br.voke.aplicacao.pessoa.CadastrarParceiroCasoDeUso;
import br.voke.aplicacao.pessoa.EditarParceiroCasoDeUso;
import br.voke.aplicacao.pessoa.ListarParceirosOrganizadorCasoDeUso;
import br.voke.aplicacao.pessoa.RemoverParceiroCasoDeUso;
import br.voke.dominio.pessoa.parceiro.AtividadeParceiro;
import br.voke.dominio.pessoa.parceiro.Parceiro;
import br.voke.dominio.pessoa.parceiro.ParceiroServico;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/parceiros")
public class ParceiroController {

    private final CadastrarParceiroCasoDeUso cadastrar;
    private final EditarParceiroCasoDeUso editar;
    private final RemoverParceiroCasoDeUso remover;
    private final ListarParceirosOrganizadorCasoDeUso listar;
    private final ParceiroServico parceiroServico;
    private final ParticipanteRepositorio participanteRepositorio;

    public ParceiroController(CadastrarParceiroCasoDeUso cadastrar,
                               EditarParceiroCasoDeUso editar,
                               RemoverParceiroCasoDeUso remover,
                               ListarParceirosOrganizadorCasoDeUso listar,
                               ParceiroServico parceiroServico,
                               ParticipanteRepositorio participanteRepositorio) {
        this.cadastrar = cadastrar;
        this.editar = editar;
        this.remover = remover;
        this.listar = listar;
        this.parceiroServico = parceiroServico;
        this.participanteRepositorio = participanteRepositorio;
    }

    record CriarReq(UUID participanteId, UUID organizadorId, Set<AtividadeParceiro> atividades) {}
    record AtividadeReq(AtividadeParceiro atividade) {}

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> criar(@RequestBody CriarReq req) {
        try {
            Parceiro p = cadastrar.executar(req.participanteId(), req.organizadorId(), req.atividades());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResposta(p));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @GetMapping("/organizador/{organizadorId}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<List<ParceiroResp>> listar(@PathVariable UUID organizadorId) {
        List<ParceiroResp> lista = listar.executar(organizadorId).stream()
                .map(this::toResposta).toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/participante/{participanteId}")
    @PreAuthorize("hasRole('PARTICIPANTE')")
    public ResponseEntity<List<ParceiroResp>> buscarPorParticipante(@PathVariable UUID participanteId) {
        List<ParceiroResp> lista = parceiroServico.buscarPorParticipante(
                new ParticipanteId(participanteId)).stream()
                .map(this::toResposta).toList();
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/{id}/atividades/adicionar")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> adicionarAtividade(@PathVariable UUID id, @RequestBody AtividadeReq req) {
        try {
            editar.adicionarAtividade(id, req.atividade());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PutMapping("/{id}/atividades/remover")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> removerAtividade(@PathVariable UUID id, @RequestBody AtividadeReq req) {
        try {
            editar.removerAtividade(id, req.atividade());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> remover(@PathVariable UUID id) {
        try {
            remover.executar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    private record ParceiroResp(String id, String participanteId, String organizadorId,
                                 Set<AtividadeParceiro> atividades, String nomeParticipante) {}

    private ParceiroResp toResposta(Parceiro p) {
        String nome = "";
        try {
            nome = participanteRepositorio.buscarPorId(p.getParticipanteId())
                    .map(part -> part.getNome().getValor()).orElse("");
        } catch (Exception ignored) {}
        return new ParceiroResp(
                p.getId().getValor().toString(),
                p.getParticipanteId().getValor().toString(),
                p.getOrganizadorId().getValor().toString(),
                p.getAtividades(),
                nome);
    }

    record ErroResp(String mensagem) {}
}
