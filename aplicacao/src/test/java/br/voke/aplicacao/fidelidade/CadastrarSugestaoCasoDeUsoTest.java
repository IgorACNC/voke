package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.sugestao.EventoConsultaGateway;
import br.voke.dominio.fidelidade.sugestao.InscricaoConsultaGateway;
import br.voke.dominio.fidelidade.sugestao.MotorSugestoes;
import br.voke.dominio.fidelidade.sugestao.PreferenciaParticipante;
import br.voke.dominio.fidelidade.sugestao.PreferenciaParticipanteId;
import br.voke.dominio.fidelidade.sugestao.PreferenciaParticipanteRepositorio;
import br.voke.dominio.fidelidade.sugestao.Sugestao;
import br.voke.dominio.fidelidade.sugestao.SugestaoId;
import br.voke.dominio.fidelidade.sugestao.SugestaoRepositorio;
import br.voke.dominio.fidelidade.sugestao.SugestaoServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CadastrarSugestaoCasoDeUsoTest {

    private InMemorySugestaoRepositorio sugestaoRepositorio;
    private CadastrarSugestaoCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        sugestaoRepositorio = new InMemorySugestaoRepositorio();
        InMemoryPreferenciaRepositorio prefRepo = new InMemoryPreferenciaRepositorio();
        StubEventoConsultaGateway eventoGateway = new StubEventoConsultaGateway();
        StubInscricaoConsultaGateway inscricaoGateway = new StubInscricaoConsultaGateway();
        MotorSugestoes motor = new MotorSugestoes(eventoGateway, inscricaoGateway);
        casoDeUso = new CadastrarSugestaoCasoDeUso(
                new SugestaoServico(sugestaoRepositorio, prefRepo, motor, eventoGateway));
    }

    @Test
    void cadastraSugestaoPersistindoNoRepositorio() {
        UUID participanteId = UUID.randomUUID();
        UUID eventoId = UUID.randomUUID();

        Sugestao sugestao = casoDeUso.executar(participanteId, eventoId, "Evento recomendado para voce");

        assertEquals(participanteId, sugestao.getParticipanteId());
        assertEquals(1, sugestaoRepositorio.salvos.size());
        Sugestao persistida = sugestaoRepositorio.salvos.get(0);
        assertEquals("Evento recomendado para voce", persistida.getDescricao());
        assertEquals(participanteId, persistida.getParticipanteId());
        assertEquals(eventoId, persistida.getEventoId());
        assertNotNull(sugestaoRepositorio.buscarPorId(persistida.getId()).orElse(null));
        assertTrue(sugestaoRepositorio.buscarPorParticipanteId(participanteId).contains(persistida));
    }

    /* ---------- stubs em memória ---------- */

    private static final class InMemorySugestaoRepositorio implements SugestaoRepositorio {
        final List<Sugestao> salvos = new ArrayList<>();
        @Override public void salvar(Sugestao s) {
            salvos.removeIf(existente -> existente.getId().equals(s.getId()));
            salvos.add(s);
        }
        @Override public Optional<Sugestao> buscarPorId(SugestaoId id) {
            return salvos.stream().filter(s -> s.getId().equals(id)).findFirst();
        }
        @Override public List<Sugestao> buscarPorParticipanteId(UUID participanteId) {
            return salvos.stream().filter(s -> s.getParticipanteId().equals(participanteId)).toList();
        }
        @Override public void remover(SugestaoId id) { salvos.removeIf(s -> s.getId().equals(id)); }
        @Override public int contarSugestoesSemanalPorParticipante(UUID participanteId) {
            return (int) salvos.stream().filter(s -> s.getParticipanteId().equals(participanteId)).count();
        }
        @Override public List<Sugestao> buscarPendentesCriadasAntesDe(int dias) { return List.of(); }
    }

    private static final class InMemoryPreferenciaRepositorio implements PreferenciaParticipanteRepositorio {
        final Map<UUID, PreferenciaParticipante> porParticipante = new HashMap<>();
        @Override public void salvar(PreferenciaParticipante p) { porParticipante.put(p.getParticipanteId(), p); }
        @Override public Optional<PreferenciaParticipante> buscarPorParticipanteId(UUID participanteId) {
            return Optional.ofNullable(porParticipante.get(participanteId));
        }
        @Override public void remover(PreferenciaParticipanteId id) {
            porParticipante.values().removeIf(p -> p.getId().equals(id));
        }
    }

    private static final class StubEventoConsultaGateway implements EventoConsultaGateway {
        @Override public Set<UUID> buscarCategoriasDoEvento(UUID eventoId) { return Set.of(); }
        @Override public boolean eventoEstaDisponivel(UUID eventoId) { return true; }
        @Override public List<EventoCandidato> buscarEventosCandidatosPorCategorias(Set<UUID> categoriaIds) {
            return List.of();
        }
    }

    private static final class StubInscricaoConsultaGateway implements InscricaoConsultaGateway {
        @Override public boolean participanteJaInscritoOuAguardando(UUID participanteId, UUID eventoId) {
            return false;
        }
    }
}
