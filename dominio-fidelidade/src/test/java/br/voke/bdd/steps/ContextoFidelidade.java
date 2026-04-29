package br.voke.bdd.steps;

import br.voke.dominio.fidelidade.carteira.CarteiraVirtual;
import br.voke.dominio.fidelidade.pontos.ContaPontos;
import br.voke.dominio.fidelidade.recompensa.Recompensa;
import br.voke.dominio.fidelidade.sugestao.Sugestao;

public class ContextoFidelidade {
    public CarteiraVirtual carteira;
    public ContaPontos conta;
    public Recompensa recompensa;
    public Sugestao sugestao;
    public Exception excecao;
}
