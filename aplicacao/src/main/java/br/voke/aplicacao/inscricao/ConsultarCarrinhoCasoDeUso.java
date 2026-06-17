package br.voke.aplicacao.inscricao;

import br.voke.dominio.inscricao.carrinho.Carrinho;
import br.voke.dominio.inscricao.carrinho.CarrinhoRepositorio;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ConsultarCarrinhoCasoDeUso {

    private final CarrinhoRepositorio repositorio;

    public ConsultarCarrinhoCasoDeUso(CarrinhoRepositorio repositorio) {
        Objects.requireNonNull(repositorio);
        this.repositorio = repositorio;
    }

    public Optional<Carrinho> executar(UUID participanteId) {
        return repositorio.buscarPorParticipanteId(participanteId);
    }
}
