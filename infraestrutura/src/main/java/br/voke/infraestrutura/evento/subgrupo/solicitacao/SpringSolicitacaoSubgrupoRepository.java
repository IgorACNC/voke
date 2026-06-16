package br.voke.infraestrutura.evento.subgrupo.solicitacao;

import br.voke.dominio.evento.subgrupo.solicitacao.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringSolicitacaoSubgrupoRepository
        extends JpaRepository<SolicitacaoSubgrupoJpa, UUID> {

    List<SolicitacaoSubgrupoJpa> findBySubgrupoId(UUID subgrupoId);

    List<SolicitacaoSubgrupoJpa> findBySubgrupoIdAndStatus(UUID subgrupoId, StatusSolicitacao status);

    List<SolicitacaoSubgrupoJpa> findByParticipanteId(UUID participanteId);

    Optional<SolicitacaoSubgrupoJpa> findFirstBySubgrupoIdAndParticipanteIdAndStatus(
            UUID subgrupoId, UUID participanteId, StatusSolicitacao status);
}
