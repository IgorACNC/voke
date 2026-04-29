package br.voke.dominio.fidelidade.pontos;

import br.voke.dominio.compartilhado.EntidadeBase;
import br.voke.dominio.fidelidade.excecao.ComprasDePontosProibidaException;
import br.voke.dominio.fidelidade.excecao.PontosInsuficientesException;

import java.util.Objects;
import java.util.UUID;

public class ContaPontos extends EntidadeBase<ContaPontosId> {

    private final UUID participanteId;
    private int saldo;

    public ContaPontos(ContaPontosId id, UUID participanteId) {
        super(id);
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        this.participanteId = participanteId;
        this.saldo = 0;
    }

    public void creditarPorPresenca(int pontos) {
        if (pontos <= 0) {
            throw new IllegalArgumentException("Pontos devem ser positivos");
        }
        this.saldo += pontos;
    }

    public void comprarPontos(int pontos) {
        throw new ComprasDePontosProibidaException();
    }

    public void debitar(int pontos) {
        if (pontos <= 0) {
            throw new IllegalArgumentException("Pontos devem ser positivos");
        }
        if (pontos > saldo) {
            throw new PontosInsuficientesException();
        }
        this.saldo -= pontos;
    }

    public void expirarPontos(int pontosExpirados) {
        if (pontosExpirados > 0 && pontosExpirados <= saldo) {
            this.saldo -= pontosExpirados;
        }
    }

    public UUID getParticipanteId() { return participanteId; }
    public int getSaldo() { return saldo; }
}