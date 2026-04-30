package br.voke.dominio.evento.cupom;

import java.util.Optional;

public interface CupomRepositorio {
    void salvar(Cupom cupom);
    Optional<Cupom> buscarPorId(CupomId id);
    Optional<Cupom> buscarPorCodigo(String codigo);
    void remover(CupomId id);
}
