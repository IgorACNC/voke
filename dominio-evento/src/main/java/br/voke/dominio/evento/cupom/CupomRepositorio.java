package br.voke.dominio.evento.cupom;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CupomRepositorio {
    void salvar(Cupom cupom);
    Optional<Cupom> buscarPorId(CupomId id);
    Optional<Cupom> buscarPorCodigo(String codigo);
    List<Cupom> buscarPorOrganizador(UUID organizadorId);
    List<Cupom> buscarGlobais();
    List<Cupom> buscarTodos();
    void remover(CupomId id);
}
