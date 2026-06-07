package br.voke.controle;

import br.voke.aplicacao.pessoa.EditarOrganizadorCasoDeUso;
import br.voke.aplicacao.pessoa.RemoverOrganizadorCasoDeUso;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/organizadores")
@PreAuthorize("hasRole('ORGANIZADOR')")
public class OrganizadorController {

    private final EditarOrganizadorCasoDeUso editarOrganizador;
    private final RemoverOrganizadorCasoDeUso removerOrganizador;

    public OrganizadorController(EditarOrganizadorCasoDeUso editarOrganizador,
                                  RemoverOrganizadorCasoDeUso removerOrganizador) {
        this.editarOrganizador = editarOrganizador;
        this.removerOrganizador = removerOrganizador;
    }

    record EditarReq(UUID id, String nome, String email) {}

    @PutMapping("/perfil")
    public ResponseEntity<?> editar(@RequestBody EditarReq req) {
        try {
            editarOrganizador.executar(req.id(), req.nome(), req.email());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable UUID id) {
        try {
            removerOrganizador.executar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    record ErroResp(String mensagem) {}
}
