package br.voke.aplicacao.evento;

import br.voke.dominio.evento.estatistica.ExportacaoConsulta;
import br.voke.dominio.evento.estatistica.LinhaFinanceiraDTO;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ExportarRelatorioFinanceiroCasoDeUso {

    private final ExportacaoConsulta consulta;

    public ExportarRelatorioFinanceiroCasoDeUso(ExportacaoConsulta consulta) {
        Objects.requireNonNull(consulta);
        this.consulta = consulta;
    }

    public List<LinhaFinanceiraDTO> executar(UUID eventoId) {
        Objects.requireNonNull(eventoId, "eventoId e obrigatorio");
        return consulta.relatorioFinanceiro(eventoId);
    }
}
