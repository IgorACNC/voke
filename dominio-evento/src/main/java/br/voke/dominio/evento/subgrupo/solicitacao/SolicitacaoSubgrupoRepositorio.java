package br.voke.dominio.evento.subgrupo.solicitacao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SolicitacaoSubgrupoRepositorio {
    void salvar(SolicitacaoSubgrupo solicitacao);
    Optional<SolicitacaoSubgrupo> buscarPorId(SolicitacaoSubgrupoId id);
    List<SolicitacaoSubgrupo> buscarPorSubgrupo(UUID subgrupoId);
    List<SolicitacaoSubgrupo> buscarPendentesPorSubgrupo(UUID subgrupoId);
    List<SolicitacaoSubgrupo> buscarPorParticipante(UUID participanteId);
    Optional<SolicitacaoSubgrupo> buscarPendentePorParticipanteESubgrupo(UUID participanteId, UUID subgrupoId);
}
