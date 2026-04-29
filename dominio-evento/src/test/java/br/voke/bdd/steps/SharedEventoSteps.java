package br.voke.bdd.steps;

import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SharedEventoSteps {
    private final ContextoEvento contexto;

    public SharedEventoSteps(ContextoEvento contexto) {
        this.contexto = contexto;
    }

    @Então("o sistema rejeita a ação")
    public void oSistemaRejeitaAAcao() {
        assertNotNull(contexto.excecao);
    }

    @E("exibe a mensagem {string}")
    public void exibeMensagem(String mensagem) {
        assertNotNull(contexto.excecao);
        assertNotNull(contexto.excecao.getMessage());
        assertTrue(contexto.excecao.getMessage().contains(mensagem),
                "Esperava mensagem contendo '" + mensagem + "', mas foi: " + contexto.excecao.getMessage());
    }
}
