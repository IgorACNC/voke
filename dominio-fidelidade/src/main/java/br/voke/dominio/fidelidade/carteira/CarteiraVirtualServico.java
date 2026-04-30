package br.voke.dominio.fidelidade.carteira;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class CarteiraVirtualServico {

    private final CarteiraVirtualRepositorio repositorio;

    public CarteiraVirtualServico(CarteiraVirtualRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
    }

    public CarteiraVirtual obterOuCriar(UUID participanteId) {
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        return repositorio.buscarPorParticipanteId(participanteId)
                .orElseGet(() -> {
                    CarteiraVirtual nova = new CarteiraVirtual(CarteiraVirtualId.novo(), participanteId);
                    repositorio.salvar(nova);
                    return nova;
                });
    }

    public void adicionarSaldo(UUID participanteId, BigDecimal valor) {
        CarteiraVirtual carteira = repositorio.buscarPorParticipanteId(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Carteira não encontrada"));
        carteira.adicionarSaldo(valor);
        repositorio.salvar(carteira);
    }

    public void removerSaldo(UUID participanteId, BigDecimal valor) {
        CarteiraVirtual carteira = repositorio.buscarPorParticipanteId(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Carteira não encontrada"));
        carteira.removerSaldo(valor);
        repositorio.salvar(carteira);
    }

    public void debitar(UUID participanteId, BigDecimal valor) {
        CarteiraVirtual carteira = repositorio.buscarPorParticipanteId(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Carteira não encontrada"));
        carteira.debitar(valor);
        repositorio.salvar(carteira);
    }

    public void creditar(UUID participanteId, BigDecimal valor) {
        CarteiraVirtual carteira = repositorio.buscarPorParticipanteId(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Carteira não encontrada"));
        carteira.creditar(valor);
        repositorio.salvar(carteira);
    }

    public BigDecimal consultarSaldo(UUID participanteId) {
        return repositorio.buscarPorParticipanteId(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Carteira não encontrada"))
                .getSaldo();
    }
}