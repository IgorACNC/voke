package br.voke.aplicacao.inscricao;

import br.voke.dominio.inscricao.excecao.ConflitoDeAgendaException;
import br.voke.dominio.inscricao.excecao.IdadeMinimaEventoException;
import br.voke.dominio.inscricao.excecao.LimiteIngressosCpfException;
import br.voke.dominio.inscricao.excecao.VagasEsgotadasException;
import br.voke.dominio.inscricao.inscricao.Inscricao;
import br.voke.dominio.inscricao.inscricao.InscricaoId;
import br.voke.dominio.inscricao.inscricao.InscricaoRepositorio;
import br.voke.dominio.inscricao.inscricao.InscricaoServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RealizarInscricaoCasoDeUsoTest {

    private InMemoryInscricaoRepositorio repositorio;
    private RealizarInscricaoCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = new InMemoryInscricaoRepositorio();
        casoDeUso = new RealizarInscricaoCasoDeUso(new InscricaoServico(repositorio));
    }

    @Test
    void realizaInscricaoUsandoRegrasDoServicoDeDominio() {
        UUID participanteId = UUID.randomUUID();
        UUID eventoId = UUID.randomUUID();

        casoDeUso.executar(participanteId, eventoId, BigDecimal.valueOf(120),
                18, 16, true, true, amanha(19), amanha(21), 2);

        assertEquals(1, repositorio.inscricoes.size());
    }

    @Test
    void rejeitaInscricaoQuandoParticipanteNaoTemIdadeMinima() {
        assertThrows(IdadeMinimaEventoException.class, () -> casoDeUso.executar(UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.valueOf(120), 15, 16, true, true, amanha(19), amanha(21), 2));
    }

    @Test
    void rejeitaInscricaoQuandoEventoNaoPossuiVagas() {
        assertThrows(VagasEsgotadasException.class, () -> casoDeUso.executar(UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.valueOf(120), 18, 16, true, false, amanha(19), amanha(21), 2));
    }

    @Test
    void rejeitaInscricaoComConflitoDeAgenda() {
        repositorio.conflitoDeHorario = true;

        assertThrows(ConflitoDeAgendaException.class, () -> casoDeUso.executar(UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.valueOf(120), 18, 16, true, true, amanha(19), amanha(21), 2));
    }

    @Test
    void rejeitaInscricaoQuandoLimitePorCpfFoiAtingido() {
        repositorio.inscricoesExistentes = 2;

        assertThrows(LimiteIngressosCpfException.class, () -> casoDeUso.executar(UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.valueOf(120), 18, 16, true, true, amanha(19), amanha(21), 2));
    }

    private LocalDateTime amanha(int hora) {
        return LocalDateTime.now().plusDays(1).withHour(hora).withMinute(0).withSecond(0).withNano(0);
    }

    private static final class InMemoryInscricaoRepositorio implements InscricaoRepositorio {
        private final List<Inscricao> inscricoes = new ArrayList<>();
        private boolean conflitoDeHorario;
        private int inscricoesExistentes;

        @Override
        public void salvar(Inscricao inscricao) {
            inscricoes.add(inscricao);
        }

        @Override
        public Optional<Inscricao> buscarPorId(InscricaoId id) {
            return inscricoes.stream().filter(inscricao -> inscricao.getId().equals(id)).findFirst();
        }

        @Override
        public List<Inscricao> buscarPorParticipanteId(UUID participanteId) {
            return inscricoes.stream().filter(inscricao -> inscricao.getParticipanteId().equals(participanteId)).toList();
        }

        @Override
        public void remover(InscricaoId id) {
            inscricoes.removeIf(inscricao -> inscricao.getId().equals(id));
        }

        @Override
        public int contarPorParticipanteEEvento(UUID participanteId, UUID eventoId) {
            return inscricoesExistentes;
        }

        @Override
        public boolean existeConflitoDeHorario(UUID participanteId, LocalDateTime inicio, LocalDateTime fim) {
            return conflitoDeHorario;
        }
    }
}
