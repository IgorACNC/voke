package br.voke.infraestrutura.evento.estatistica;

import br.voke.dominio.evento.estatistica.EstatisticaEvento;
import br.voke.dominio.evento.estatistica.EstatisticaEventoId;
import br.voke.infraestrutura.compartilhado.DominioReflection;

public final class EstatisticaEventoJpaMapper {

    private EstatisticaEventoJpaMapper() {}

    public static EstatisticaEventoJpa paraJpa(EstatisticaEvento e) {
        return new EstatisticaEventoJpa(
                e.getId().getValor(),
                e.getEventoId(),
                e.getOrganizadorId(),
                e.getIngressosVendidos(),
                e.getReceitaConsolidada(),
                e.getCheckInsRealizados(),
                e.getAusencias(),
                e.getCuponsUtilizados(),
                e.getDescontoAcumulado(),
                e.getVisualizacoes(),
                e.estaCongelada(),
                e.getAtualizadoEm());
    }

    public static EstatisticaEvento paraDominio(EstatisticaEventoJpa j) {
        EstatisticaEvento e = new EstatisticaEvento(
                new EstatisticaEventoId(j.getId()),
                j.getEventoId(),
                j.getOrganizadorId());
        DominioReflection.definirCampo(e, "ingressosVendidos", j.getIngressosVendidos());
        DominioReflection.definirCampo(e, "receitaConsolidada", j.getReceitaConsolidada());
        DominioReflection.definirCampo(e, "checkInsRealizados", j.getCheckInsRealizados());
        DominioReflection.definirCampo(e, "ausencias", j.getAusencias());
        DominioReflection.definirCampo(e, "cuponsUtilizados", j.getCuponsUtilizados());
        DominioReflection.definirCampo(e, "descontoAcumulado", j.getDescontoAcumulado());
        DominioReflection.definirCampo(e, "visualizacoes", j.getVisualizacoes());
        DominioReflection.definirCampo(e, "congelado", j.isCongelado());
        DominioReflection.definirCampo(e, "atualizadoEm", j.getAtualizadoEm());
        return e;
    }
}
