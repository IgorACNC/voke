package br.voke.dominio.fidelidade.pontos;

public class GanhoPontosEventoEspecial implements EstrategiaGanhoPontos {

    @Override
    public int calcular(int pontosBase) {
        return pontosBase * 2;
    }
}
