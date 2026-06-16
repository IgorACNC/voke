package br.voke.infraestrutura.evento.subgrupo.solicitacao;

import br.voke.dominio.evento.subgrupo.solicitacao.SolicitacaoSubgrupo;
import br.voke.dominio.evento.subgrupo.solicitacao.SolicitacaoSubgrupoId;
import br.voke.dominio.evento.subgrupo.solicitacao.SolicitacaoSubgrupoRepositorio;
import br.voke.dominio.evento.subgrupo.solicitacao.StatusSolicitacao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SolicitacaoSubgrupoRepositorioJpa implements SolicitacaoSubgrupoRepositorio {

    private final SpringSolicitacaoSubgrupoRepository repository;

    public SolicitacaoSubgrupoRepositorioJpa(SpringSolicitacaoSubgrupoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void salvar(SolicitacaoSubgrupo solicitacao) {
        repository.save(SolicitacaoSubgrupoJpaMapper.paraJpa(solicitacao));
    }

    @Override
    public Optional<SolicitacaoSubgrupo> buscarPorId(SolicitacaoSubgrupoId id) {
        return repository.findById(id.getValor()).map(SolicitacaoSubgrupoJpaMapper::paraDominio);
    }

    @Override
    public List<SolicitacaoSubgrupo> buscarPorSubgrupo(UUID subgrupoId) {
        return repository.findBySubgrupoId(subgrupoId).stream()
                .map(SolicitacaoSubgrupoJpaMapper::paraDominio)
                .toList();
    }

    @Override
    public List<SolicitacaoSubgrupo> buscarPendentesPorSubgrupo(UUID subgrupoId) {
        return repository.findBySubgrupoIdAndStatus(subgrupoId, StatusSolicitacao.PENDENTE).stream()
                .map(SolicitacaoSubgrupoJpaMapper::paraDominio)
                .toList();
    }

    @Override
    public List<SolicitacaoSubgrupo> buscarPorParticipante(UUID participanteId) {
        return repository.findByParticipanteId(participanteId).stream()
                .map(SolicitacaoSubgrupoJpaMapper::paraDominio)
                .toList();
    }

    @Override
    public Optional<SolicitacaoSubgrupo> buscarPendentePorParticipanteESubgrupo(UUID participanteId, UUID subgrupoId) {
        return repository
                .findFirstBySubgrupoIdAndParticipanteIdAndStatus(
                        subgrupoId, participanteId, StatusSolicitacao.PENDENTE)
                .map(SolicitacaoSubgrupoJpaMapper::paraDominio);
    }
}
