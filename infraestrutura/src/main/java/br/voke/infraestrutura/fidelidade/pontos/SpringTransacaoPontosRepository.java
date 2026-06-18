package br.voke.infraestrutura.fidelidade.pontos;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringTransacaoPontosRepository extends JpaRepository<TransacaoPontosJpa, UUID> {
    List<TransacaoPontosJpa> findByParticipanteIdOrderByDataHoraDesc(UUID participanteId);
}
