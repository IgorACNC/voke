package br.voke.aplicacao.evento;

import br.voke.dominio.evento.estatistica.ExportacaoConsulta;
import br.voke.dominio.evento.estatistica.LinhaPresencaDTO;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ExportarListaPresencaCasoDeUso {

    private final ExportacaoConsulta consulta;

    public ExportarListaPresencaCasoDeUso(ExportacaoConsulta consulta) {
        Objects.requireNonNull(consulta);
        this.consulta = consulta;
    }

    public List<LinhaPresencaDTO> executar(UUID eventoId) {
        Objects.requireNonNull(eventoId, "eventoId e obrigatorio");
        return consulta.listaPresenca(eventoId);
    }
}
