package br.voke.infraestrutura.fidelidade.carteira;

import br.voke.dominio.fidelidade.carteira.CarteiraVirtual;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualId;
import br.voke.infraestrutura.compartilhado.DominioReflection;

public final class CarteiraVirtualJpaMapper {

    private CarteiraVirtualJpaMapper() {}

    public static CarteiraVirtualJpa paraJpa(CarteiraVirtual carteira) {
        return new CarteiraVirtualJpa(
                carteira.getId().getValor(),
                carteira.getParticipanteId(),
                carteira.getSaldo(),
                carteira.getSaldoPromocional(),
                carteira.getTotalInseridoHoje(),
                carteira.getContadorSaquesHoje(),
                carteira.getDataContador());
    }

    public static CarteiraVirtual paraDominio(CarteiraVirtualJpa jpa) {
        CarteiraVirtual carteira = new CarteiraVirtual(new CarteiraVirtualId(jpa.getId()), jpa.getParticipanteId());
        DominioReflection.definirCampo(carteira, "saldo", jpa.getSaldo());
        DominioReflection.definirCampo(carteira, "saldoPromocional", jpa.getSaldoPromocional());
        DominioReflection.definirCampo(carteira, "totalInseridoHoje", jpa.getTotalInseridoHoje());
        DominioReflection.definirCampo(carteira, "contadorSaquesHoje", jpa.getContadorSaquesHoje());
        DominioReflection.definirCampo(carteira, "dataContador", jpa.getDataContador());
        return carteira;
    }
}
