package br.voke.dominio.inscricao.carrinho;

import java.util.Optional;
import java.util.UUID;

public interface CarrinhoRepositorio {
    void salvar(Carrinho carrinho);
    Optional<Carrinho> buscarPorId(CarrinhoId id);
    Optional<Carrinho> buscarPorParticipanteId(UUID participanteId);
    void remover(CarrinhoId id);
}