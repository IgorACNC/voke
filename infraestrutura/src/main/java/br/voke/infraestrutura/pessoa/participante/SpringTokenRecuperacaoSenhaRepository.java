package br.voke.infraestrutura.pessoa.participante;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringTokenRecuperacaoSenhaRepository extends JpaRepository<TokenRecuperacaoSenhaJpa, UUID> {
    Optional<TokenRecuperacaoSenhaJpa> findByToken(String token);
}
