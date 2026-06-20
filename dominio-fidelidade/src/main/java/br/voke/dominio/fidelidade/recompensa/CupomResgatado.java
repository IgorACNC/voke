package br.voke.dominio.fidelidade.recompensa;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CupomResgatado(UUID id, UUID participanteId, String codigoCupom,
                              UUID recompensaId, String recompensaNome, BigDecimal valor,
                              UUID organizadorId, LocalDateTime dataResgate) {
    public boolean isGlobal() {
        return organizadorId == null;
    }
}
