package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.transacao.TransacaoFinanceira;
import br.voke.dominio.fidelidade.transacao.TransacaoFinanceiraRepositorio;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ConsultarExtratoCasoDeUso {

    private final TransacaoFinanceiraRepositorio repositorio;

    public ConsultarExtratoCasoDeUso(TransacaoFinanceiraRepositorio repositorio) {
        Objects.requireNonNull(repositorio);
        this.repositorio = repositorio;
    }

    public List<TransacaoFinanceira> executar(UUID participanteId) {
        return repositorio.buscarPorParticipanteId(participanteId);
    }
}
