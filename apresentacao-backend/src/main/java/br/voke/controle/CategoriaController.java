package br.voke.controle;

import br.voke.aplicacao.evento.ListarCategoriasCasoDeUso;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final ListarCategoriasCasoDeUso listar;

    public CategoriaController(ListarCategoriasCasoDeUso listar) {
        this.listar = listar;
    }

    record CategoriaResp(UUID id, String nome) {}

    @GetMapping
    public ResponseEntity<List<CategoriaResp>> listar() {
        List<CategoriaResp> resp = listar.executar().stream()
                .map(c -> new CategoriaResp(c.getId().getValor(), c.getNome()))
                .toList();
        return ResponseEntity.ok(resp);
    }
}
