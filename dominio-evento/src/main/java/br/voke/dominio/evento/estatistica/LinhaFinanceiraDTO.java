package br.voke.dominio.evento.estatistica;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LinhaFinanceiraDTO(
        LocalDateTime dataInscricao,
        String codigoValidador,
        BigDecimal valorPago,
        String status
) {}
