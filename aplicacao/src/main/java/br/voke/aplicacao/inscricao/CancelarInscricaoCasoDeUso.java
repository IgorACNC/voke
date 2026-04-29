package br.voke.aplicacao.inscricao;

import br.voke.dominio.inscricao.inscricao.InscricaoId;
import br.voke.dominio.inscricao.inscricao.InscricaoServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class CancelarInscricaoCasoDeUso {

    private final InscricaoServico servico;

    public CancelarInscricaoCasoDeUso(InscricaoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public BigDecimal executar(UUID inscricaoId, LocalDateTime dataEvento) {
        return servico.cancelar(new InscricaoId(inscricaoId), dataEvento);
    }
}
