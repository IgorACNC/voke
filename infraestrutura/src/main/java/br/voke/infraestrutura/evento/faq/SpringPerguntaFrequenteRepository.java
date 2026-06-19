package br.voke.infraestrutura.evento.faq;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringPerguntaFrequenteRepository extends JpaRepository<PerguntaFrequenteJpa, UUID> {
    List<PerguntaFrequenteJpa> findByEventoIdOrderByPosicaoAsc(UUID eventoId);
    long countByEventoId(UUID eventoId);
    boolean existsByEventoIdAndPerguntaNormalizada(UUID eventoId, String perguntaNormalizada);
    boolean existsByEventoIdAndPerguntaNormalizadaAndIdNot(UUID eventoId, String perguntaNormalizada, UUID id);
}
