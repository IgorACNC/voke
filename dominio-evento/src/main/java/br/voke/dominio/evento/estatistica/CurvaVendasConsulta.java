package br.voke.dominio.evento.estatistica;

import java.util.List;
import java.util.UUID;

public interface CurvaVendasConsulta {
    List<PontoCurvaVendas> curvaVendas(UUID eventoId);
}
