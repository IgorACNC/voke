package br.voke.dominio.evento.estatistica;

/**
 * DTO de exportacao da lista de presenca (RN04 - LGPD).
 * Contem apenas dados operacionais necessarios para controle de portaria.
 * Nenhum CPF completo, telefone ou dado bancario.
 */
public record LinhaPresencaDTO(
        String nome,
        String email,
        String cpfMascarado,
        String tipoIngresso,
        String codigoValidador,
        String statusCheckIn
) {}
