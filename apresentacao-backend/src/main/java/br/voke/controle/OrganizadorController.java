package br.voke.controle;

import br.voke.aplicacao.pessoa.EditarOrganizadorCasoDeUso;
import br.voke.aplicacao.pessoa.RemoverOrganizadorCasoDeUso;
import br.voke.dominio.compartilhado.Senha;
import br.voke.dominio.pessoa.organizador.Organizador;
import br.voke.dominio.pessoa.organizador.OrganizadorId;
import br.voke.dominio.pessoa.organizador.OrganizadorRepositorio;
import br.voke.dominio.pessoa.organizador.OrganizadorServico;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/organizadores")
public class OrganizadorController {

    private final EditarOrganizadorCasoDeUso editarOrganizador;
    private final RemoverOrganizadorCasoDeUso removerOrganizador;
    private final OrganizadorRepositorio organizadorRepositorio;
    private final OrganizadorServico organizadorServico;
    private final PasswordEncoder passwordEncoder;

    public OrganizadorController(EditarOrganizadorCasoDeUso editarOrganizador,
                                  RemoverOrganizadorCasoDeUso removerOrganizador,
                                  OrganizadorRepositorio organizadorRepositorio,
                                  OrganizadorServico organizadorServico,
                                  PasswordEncoder passwordEncoder) {
        this.editarOrganizador = editarOrganizador;
        this.removerOrganizador = removerOrganizador;
        this.organizadorRepositorio = organizadorRepositorio;
        this.organizadorServico = organizadorServico;
        this.passwordEncoder = passwordEncoder;
    }

    record EditarReq(UUID id, String nome, String email) {}
    record AlterarSenhaReq(UUID id, String senhaAtual, String novaSenha) {}
    record PerfilPublicoResp(UUID id, String nome, String email) {}
    record PerfilCompletoResp(UUID id, String nome, String email, String cpf, String dataNascimento) {}
    record ErroResp(String mensagem) {}

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPerfilPublico(@PathVariable UUID id) {
        return organizadorRepositorio.buscarPorId(new OrganizadorId(id))
                .map(o -> ResponseEntity.ok((Object) new PerfilPublicoResp(
                        o.getId().getValor(), o.getNome().getValor(), o.getEmail().getValor())))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> buscarPerfilCompleto(@PathVariable UUID id) {
        return organizadorRepositorio.buscarPorId(new OrganizadorId(id))
                .map(o -> ResponseEntity.ok((Object) new PerfilCompletoResp(
                        o.getId().getValor(),
                        o.getNome().getValor(),
                        o.getEmail().getValor(),
                        o.getCpf().getValor(),
                        o.getDataNascimento().getValor().toString())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/perfil")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> editar(@RequestBody EditarReq req) {
        try {
            editarOrganizador.executar(req.id(), req.nome(), req.email());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @PutMapping("/senha")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> alterarSenha(@RequestBody AlterarSenhaReq req) {
        try {
            Organizador o = organizadorRepositorio.buscarPorId(new OrganizadorId(req.id()))
                    .orElseThrow(() -> new IllegalArgumentException("Organizador não encontrado"));
            if (!passwordEncoder.matches(req.senhaAtual(), o.getSenha().getValor())) {
                return ResponseEntity.badRequest().body(new ErroResp("Senha atual incorreta"));
            }
            new Senha(req.novaSenha());
            String hash = passwordEncoder.encode(req.novaSenha());
            organizadorServico.atualizarSenha(new OrganizadorId(req.id()), new Senha(hash));
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> remover(@PathVariable UUID id) {
        try {
            removerOrganizador.executar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }
}
