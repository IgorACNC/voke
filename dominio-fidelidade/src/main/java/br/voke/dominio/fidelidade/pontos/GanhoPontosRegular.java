package br.voke.dominio.fidelidade.pontos;

public class GanhoPontosRegular implements EstrategiaGanhoPontos {

    @Override
    public int calcular(int pontosBase) {
        return pontosBase;
    }
}
