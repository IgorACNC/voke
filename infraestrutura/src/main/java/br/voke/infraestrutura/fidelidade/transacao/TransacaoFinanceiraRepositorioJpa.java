package br.voke.infraestrutura.fidelidade.transacao;

import br.voke.dominio.fidelidade.transacao.TransacaoFinanceira;
import br.voke.dominio.fidelidade.transacao.TransacaoFinanceiraRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class TransacaoFinanceiraRepositorioJpa implements TransacaoFinanceiraRepositorio {

    private final SpringTransacaoFinanceiraRepository repository;

    public TransacaoFinanceiraRepositorioJpa(SpringTransacaoFinanceiraRepository repository) {
        this.repository = repository;
    }

    @Override
    public void salvar(TransacaoFinanceira transacao) {
        repository.save(TransacaoFinanceiraJpaMapper.paraJpa(transacao));
    }

    @Override
    public List<TransacaoFinanceira> buscarPorParticipanteId(UUID participanteId) {
        return repository.findByParticipanteIdOrderByDataHoraDesc(participanteId)
                .stream()
                .map(TransacaoFinanceiraJpaMapper::paraDominio)
                .toList();
    }
}
