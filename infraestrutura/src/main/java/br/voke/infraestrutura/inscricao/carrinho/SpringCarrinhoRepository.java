package br.voke.infraestrutura.inscricao.carrinho;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringCarrinhoRepository extends JpaRepository<CarrinhoJpa, UUID> {
    Optional<CarrinhoJpa> findByParticipanteId(UUID participanteId);

    @Query("SELECT c FROM CarrinhoJpa c WHERE c.criadoEm < :limite")
    List<CarrinhoJpa> buscarExpirados(@Param("limite") LocalDateTime limite);
}