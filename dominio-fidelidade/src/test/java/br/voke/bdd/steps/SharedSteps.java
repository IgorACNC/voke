package br.voke.bdd.steps;

import br.voke.dominio.fidelidade.carteira.CarteiraVirtual;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualId;
import br.voke.dominio.fidelidade.pontos.ContaPontos;
import br.voke.dominio.fidelidade.pontos.ContaPontosId;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SharedSteps {
    private final ContextoFidelidade contexto;

    public SharedSteps(ContextoFidelidade contexto) {
        this.contexto = contexto;
    }

    @Dado("que o participante está autenticado")
    public void participanteAutenticado() {
        UUID participanteId = UUID.randomUUID();
        contexto.carteira = new CarteiraVirtual(CarteiraVirtualId.novo(), participanteId);
        contexto.conta = new ContaPontos(ContaPontosId.novo(), participanteId);
        contexto.excecao = null;
    }

    @E("exibe a mensagem {string}")
    public void exibeMensagem(String msg) {
        assertNotNull(contexto.excecao);
        assertTrue(contexto.excecao.getMessage() != null && contexto.excecao.getMessage().contains(msg),
                "Mensagem esperada '" + msg + "' não encontrada em: " + contexto.excecao.getMessage());
    }
}
