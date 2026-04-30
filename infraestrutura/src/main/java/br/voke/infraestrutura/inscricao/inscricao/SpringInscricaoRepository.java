package br.voke.infraestrutura.inscricao.inscricao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringInscricaoRepository extends JpaRepository<InscricaoJpa, UUID> {
    List<InscricaoJpa> findByParticipanteId(UUID participanteId);
    int countByParticipanteIdAndEventoId(UUID participanteId, UUID eventoId);
}
