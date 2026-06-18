package br.voke.aplicacao.evento;

import br.voke.dominio.evento.estatistica.CurvaVendasConsulta;
import br.voke.dominio.evento.estatistica.PontoCurvaVendas;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ConsultarCurvaVendasCasoDeUso {

    private final CurvaVendasConsulta consulta;

    public ConsultarCurvaVendasCasoDeUso(CurvaVendasConsulta consulta) {
        Objects.requireNonNull(consulta);
        this.consulta = consulta;
    }

    public List<PontoCurvaVendas> executar(UUID eventoId) {
        Objects.requireNonNull(eventoId, "eventoId e obrigatorio");
        return consulta.curvaVendas(eventoId);
    }
}
