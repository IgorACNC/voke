package br.voke.controle;

import br.voke.aplicacao.evento.CadastrarCategoriaCasoDeUso;
import br.voke.aplicacao.evento.EditarCategoriaCasoDeUso;
import br.voke.aplicacao.evento.ListarCategoriasCasoDeUso;
import br.voke.aplicacao.evento.RemoverCategoriaCasoDeUso;
import br.voke.dominio.evento.categoria.Categoria;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final CadastrarCategoriaCasoDeUso cadastrar;
    private final EditarCategoriaCasoDeUso editar;
    private final RemoverCategoriaCasoDeUso remover;
    private final ListarCategoriasCasoDeUso listar;

    public AdminController(CadastrarCategoriaCasoDeUso cadastrar,
                           EditarCategoriaCasoDeUso editar,
                           RemoverCategoriaCasoDeUso remover,
                           ListarCategoriasCasoDeUso listar) {
        this.cadastrar = cadastrar;
        this.editar = editar;
        this.remover = remover;
        this.listar = listar;
    }

    record CategoriaReq(String nome) {}
    record CategoriaResp(UUID id, String nome) {}
    record ErroResp(String mensagem) {}

    @PostMapping("/categorias")
    public ResponseEntity<?> cadastrarCategoria(@RequestBody CategoriaReq req) {
        try {
            Categoria c = cadastrar.executar(req.nome());
            return ResponseEntity.ok(new CategoriaResp(c.getId().getValor(), c.getNome()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaResp>> listar() {
        List<CategoriaResp> resp = listar.executar().stream()
                .map(c -> new CategoriaResp(c.getId().getValor(), c.getNome()))
                .toList();
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/categorias/{id}")
    public ResponseEntity<?> editarCategoria(@PathVariable UUID id, @RequestBody CategoriaReq req) {
        try {
            editar.executar(id, req.nome());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }

    @DeleteMapping("/categorias/{id}")
    public ResponseEntity<?> removerCategoria(@PathVariable UUID id) {
        try {
            remover.executar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResp(e.getMessage()));
        }
    }
}
