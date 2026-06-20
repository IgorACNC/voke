package br.voke.dominio.fidelidade.pontos;

public class GanhoPontosCheckInBonus implements EstrategiaGanhoPontos {

    @Override
    public int calcular(int pontosBase) {
        return (int) Math.round(pontosBase * 1.5);
    }
}
