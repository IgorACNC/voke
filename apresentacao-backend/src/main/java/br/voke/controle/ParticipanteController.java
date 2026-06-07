package br.voke.controle;

import br.voke.aplicacao.pessoa.EditarParticipanteCasoDeUso;
import br.voke.aplicacao.pessoa.RemoverParticipanteCasoDeUso;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/participantes")
@PreAuthorize("hasRole('PARTICIPANTE')")
public class ParticipanteController {

    private final EditarParticipanteCasoDeUso editarParticipante;
    private final RemoverParticipanteCasoDeUso removerParticipante;

    public ParticipanteController(EditarParticipanteCasoDeUso editarParticipante,
                                   RemoverParticipanteCasoDeUso removerParticipante) {
        this.editarParticipante = editarParticipante;
        this.removerParticipante = removerParticipante;
    }

    record EditarReq(UUID id, String nome, String email) {}

    @PutMapping("/perfil")
    public ResponseEntity<?> editar(@RequestBody EditarReq req) {
        try {
            editarParticipante.executar(req.id(), req.nome(), req.email());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable UUID id) {
        try {
            removerParticipante.executar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    record ErroResp(String mensagem) {}
}
