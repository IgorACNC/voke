package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.excecao.PontosInsuficientesException;
import br.voke.dominio.fidelidade.pontos.ContaPontos;
import br.voke.dominio.fidelidade.pontos.ContaPontosId;
import br.voke.dominio.fidelidade.recompensa.Recompensa;
import br.voke.dominio.fidelidade.recompensa.RecompensaId;
import br.voke.dominio.fidelidade.recompensa.RecompensaRepositorio;
import br.voke.dominio.fidelidade.recompensa.RecompensaServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResgatarRecompensaCasoDeUsoTest {

    private CreditarPontosCasoDeUsoTest.InMemoryContaPontosRepositorio contaRepositorio;
    private InMemoryRecompensaRepositorio recompensaRepositorio;
    private ResgatarRecompensaCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        contaRepositorio = new CreditarPontosCasoDeUsoTest.InMemoryContaPontosRepositorio();
        recompensaRepositorio = new InMemoryRecompensaRepositorio();
        casoDeUso = new ResgatarRecompensaCasoDeUso(
                new RecompensaServico(recompensaRepositorio, contaRepositorio));
    }

    @Test
    void debitaPontosEResgataRecompensaUsandoRegrasDoDominio() {
        UUID participanteId = UUID.randomUUID();
        ContaPontos conta = new ContaPontos(ContaPontosId.novo(), participanteId);
        conta.creditarPorPresenca(100);
        contaRepositorio.salvar(conta);
        Recompensa recompensa = recompensa(70);
        recompensaRepositorio.salvar(recompensa);

        casoDeUso.executar(participanteId, recompensa.getId().getValor());

        assertEquals(30, conta.getSaldo());
        assertEquals(1, recompensa.getEstoqueResgatado());
    }

    @Test
    void rejeitaResgateQuandoSaldoEInsuficiente() {
        UUID participanteId = UUID.randomUUID();
        ContaPontos conta = new ContaPontos(ContaPontosId.novo(), participanteId);
        conta.creditarPorPresenca(20);
        contaRepositorio.salvar(conta);
        Recompensa recompensa = recompensa(70);
        recompensaRepositorio.salvar(recompensa);

        assertThrows(PontosInsuficientesException.class,
                () -> casoDeUso.executar(participanteId, recompensa.getId().getValor()));
    }

    private Recompensa recompensa(int custo) {
        return new Recompensa(RecompensaId.novo(), "Ingresso VIP", "Beneficio", custo, 2, UUID.randomUUID());
    }

    private static final class InMemoryRecompensaRepositorio implements RecompensaRepositorio {
        private final Map<RecompensaId, Recompensa> recompensas = new HashMap<>();

        @Override
        public void salvar(Recompensa recompensa) {
            recompensas.put(recompensa.getId(), recompensa);
        }

        @Override
        public Optional<Recompensa> buscarPorId(RecompensaId id) {
            return Optional.ofNullable(recompensas.get(id));
        }

        @Override
        public List<Recompensa> buscarPorOrganizadorId(UUID organizadorId) {
            return recompensas.values().stream()
                    .filter(recompensa -> recompensa.getOrganizadorId().equals(organizadorId))
                    .toList();
        }

        @Override
        public void remover(RecompensaId id) {
            recompensas.remove(id);
        }
    }
}
