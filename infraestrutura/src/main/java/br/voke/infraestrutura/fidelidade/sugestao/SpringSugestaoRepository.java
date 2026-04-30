package br.voke.infraestrutura.fidelidade.sugestao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringSugestaoRepository extends JpaRepository<SugestaoJpa, UUID> {
    List<SugestaoJpa> findByParticipanteId(UUID participanteId);
    int countByParticipanteIdAndCriadaEmAfter(UUID participanteId, LocalDateTime inicioSemana);
}
