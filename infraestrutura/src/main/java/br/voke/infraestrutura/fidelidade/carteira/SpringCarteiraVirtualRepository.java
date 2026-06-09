package br.voke.infraestrutura.fidelidade.carteira;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SpringCarteiraVirtualRepository extends JpaRepository<CarteiraVirtualJpa, UUID> {
    Optional<CarteiraVirtualJpa> findByParticipanteId(UUID participanteId);

    @Modifying
    @Transactional
    @Query("UPDATE CarteiraVirtualJpa c SET c.totalInseridoHoje = 0, c.contadorSaquesHoje = 0")
    void resetarLimitesDiarios();
}
