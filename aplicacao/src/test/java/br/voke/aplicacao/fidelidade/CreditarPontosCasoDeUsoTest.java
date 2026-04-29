package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.pontos.ContaPontos;
import br.voke.dominio.fidelidade.pontos.ContaPontosId;
import br.voke.dominio.fidelidade.pontos.ContaPontosRepositorio;
import br.voke.dominio.fidelidade.pontos.ContaPontosServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreditarPontosCasoDeUsoTest {

    private InMemoryContaPontosRepositorio repositorio;
    private CreditarPontosCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = new InMemoryContaPontosRepositorio();
        casoDeUso = new CreditarPontosCasoDeUso(new ContaPontosServico(repositorio));
    }

    @Test
    void creditaPontosSomenteQuandoEventoEncerradoECheckInFoiRealizado() {
        UUID participanteId = UUID.randomUUID();
        repositorio.salvar(new ContaPontos(ContaPontosId.novo(), participanteId));

        casoDeUso.executar(participanteId, 50, true, true);

        assertEquals(50, repositorio.buscarPorParticipanteId(participanteId).orElseThrow().getSaldo());
    }

    @Test
    void rejeitaCreditoQuandoEventoAindaNaoFoiEncerrado() {
        assertThrows(IllegalStateException.class,
                () -> casoDeUso.executar(UUID.randomUUID(), 50, false, true));
    }

    @Test
    void rejeitaCreditoQuandoCheckInNaoFoiRealizado() {
        assertThrows(IllegalStateException.class,
                () -> casoDeUso.executar(UUID.randomUUID(), 50, true, false));
    }

    static final class InMemoryContaPontosRepositorio implements ContaPontosRepositorio {
        private final Map<ContaPontosId, ContaPontos> contas = new HashMap<>();

        @Override
        public void salvar(ContaPontos conta) {
            contas.put(conta.getId(), conta);
        }

        @Override
        public Optional<ContaPontos> buscarPorId(ContaPontosId id) {
            return Optional.ofNullable(contas.get(id));
        }

        @Override
        public Optional<ContaPontos> buscarPorParticipanteId(UUID participanteId) {
            return contas.values().stream()
                    .filter(conta -> conta.getParticipanteId().equals(participanteId))
                    .findFirst();
        }
    }
}