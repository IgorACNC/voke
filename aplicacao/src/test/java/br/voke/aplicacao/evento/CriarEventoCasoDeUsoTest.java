package br.voke.aplicacao.evento;

import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.evento.EventoServico;
import br.voke.dominio.evento.excecao.ColisaoDeEspacoException;
import br.voke.dominio.evento.excecao.NomeDuplicadoException;
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

class CriarEventoCasoDeUsoTest {

    private InMemoryEventoRepositorio repositorio;
    private CriarEventoCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = new InMemoryEventoRepositorio();
        casoDeUso = new CriarEventoCasoDeUso(new EventoServico(repositorio));
    }

    @Test
    void criaEventoComLoteInicialPassandoPeloServicoDeDominio() {
        Evento evento = casoDeUso.executar("Recife Tech", "Conferencia", "Sala 1",
                amanha(9), amanha(12), 100, UUID.randomUUID(), BigDecimal.valueOf(80), 50, 16);

        assertEquals("Recife Tech", evento.getNome());
        assertEquals(50, evento.getLoteAtual().getQuantidadeTotal());
        assertEquals(1, repositorio.eventos.size());
    }

    @Test
    void rejeitaEventoComNomeDuplicado() {
        repositorio.nomeDuplicado = true;

        assertThrows(NomeDuplicadoException.class, () -> casoDeUso.executar("Recife Tech", "Conferencia", "Sala 1",
                amanha(9), amanha(12), 100, UUID.randomUUID(), BigDecimal.valueOf(80), 50, 16));
    }

    @Test
    void rejeitaEventoComColisaoDeEspaco() {
        repositorio.eventosEmConflito.add(new Evento(EventoId.novo(), "Outro", "Evento", "Sala 1",
                amanha(10), amanha(11), 100, UUID.randomUUID(),
                new br.voke.dominio.evento.evento.Lote(1, BigDecimal.TEN, 10), 16));

        assertThrows(ColisaoDeEspacoException.class, () -> casoDeUso.executar("Recife Tech", "Conferencia", "Sala 1",
                amanha(9), amanha(12), 100, UUID.randomUUID(), BigDecimal.valueOf(80), 50, 16));
    }

    private LocalDateTime amanha(int hora) {
        return LocalDateTime.now().plusDays(1).withHour(hora).withMinute(0).withSecond(0).withNano(0);
    }

    private static final class InMemoryEventoRepositorio implements EventoRepositorio {
        private final List<Evento> eventos = new ArrayList<>();
        private final List<Evento> eventosEmConflito = new ArrayList<>();
        private boolean nomeDuplicado;

        @Override
        public void salvar(Evento evento) {
            eventos.add(evento);
        }

        @Override
        public Optional<Evento> buscarPorId(EventoId id) {
            return eventos.stream().filter(evento -> evento.getId().equals(id)).findFirst();
        }

        @Override
        public Optional<Evento> buscarPorNome(String nome) {
            return eventos.stream().filter(evento -> evento.getNome().equals(nome)).findFirst();
        }

        @Override
        public List<Evento> buscarPorLocalEPeriodo(String local, LocalDateTime inicio, LocalDateTime fim) {
            return new ArrayList<>(eventosEmConflito);
        }

        @Override
        public void remover(EventoId id) {
            eventos.removeIf(evento -> evento.getId().equals(id));
        }

        @Override
        public boolean existePorNome(String nome) {
            return nomeDuplicado;
        }
    }
}
