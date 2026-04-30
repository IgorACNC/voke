package br.voke.infraestrutura.inscricao.carrinho;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringCarrinhoRepository extends JpaRepository<CarrinhoJpa, UUID> {
    Optional<CarrinhoJpa> findByParticipanteId(UUID participanteId);
}