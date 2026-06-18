package br.voke.dominio.evento.estatistica;

import java.util.List;
import java.util.UUID;

public interface ExportacaoConsulta {
    List<LinhaPresencaDTO> listaPresenca(UUID eventoId);
    List<LinhaFinanceiraDTO> relatorioFinanceiro(UUID eventoId);
}
