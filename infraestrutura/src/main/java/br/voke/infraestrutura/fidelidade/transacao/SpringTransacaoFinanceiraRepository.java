package br.voke.infraestrutura.fidelidade.transacao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringTransacaoFinanceiraRepository extends JpaRepository<TransacaoFinanceiraJpa, UUID> {
    List<TransacaoFinanceiraJpa> findByParticipanteIdOrderByDataHoraDesc(UUID participanteId);
}
