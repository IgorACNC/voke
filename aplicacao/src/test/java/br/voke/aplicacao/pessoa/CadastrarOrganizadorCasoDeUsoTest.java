package br.voke.aplicacao.pessoa;

import br.voke.dominio.compartilhado.Cpf;
import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.pessoa.excecao.EmailDuplicadoException;
import br.voke.dominio.pessoa.organizador.EventosOrganizadorGateway;
import br.voke.dominio.pessoa.organizador.Organizador;
import br.voke.dominio.pessoa.organizador.OrganizadorId;
import br.voke.dominio.pessoa.organizador.OrganizadorRepositorio;
import br.voke.dominio.pessoa.organizador.OrganizadorServico;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CadastrarOrganizadorCasoDeUsoTest {

    private InMemoryOrganizadorRepositorio repositorio;
    private InMemoryParticipanteRepositorio participanteRepositorio;
    private SetEventosOrganizadorGateway eventosGateway;
    private CadastrarOrganizadorCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = new InMemoryOrganizadorRepositorio();
        participanteRepositorio = new InMemoryParticipanteRepositorio();
        eventosGateway = new SetEventosOrganizadorGateway();
        casoDeUso = new CadastrarOrganizadorCasoDeUso(
                new OrganizadorServico(repositorio, participanteRepositorio, eventosGateway));
    }

    @Test
    void cadastraOrganizadorAplicandoRegrasDoDominio() {
        Organizador organizador = casoDeUso.executar("Bruno Lima", "52998224725",
                "bruno@voke.com", "Senha123", LocalDate.now().minusYears(30));

        assertEquals("bruno@voke.com", organizador.getEmail().getValor());
        assertNotNull(repositorio.buscarPorId(organizador.getId()).orElse(null));
    }

    @Test
    void rejeitaOrganizadorComEmailDuplicado() {
        repositorio.emailsExistentes.add(new Email("bruno@voke.com"));

        assertThrows(EmailDuplicadoException.class, () -> casoDeUso.executar("Bruno Lima", "52998224725",
                "bruno@voke.com", "Senha123", LocalDate.now().minusYears(30)));

        assertTrue(repositorio.salvos.isEmpty());
    }

    /* ---------- stubs em memória ---------- */

    private static final class InMemoryOrganizadorRepositorio implements OrganizadorRepositorio {
        final List<Organizador> salvos = new ArrayList<>();
        final Set<Cpf> cpfsExistentes = new HashSet<>();
        final Set<Email> emailsExistentes = new HashSet<>();

        @Override public void salvar(Organizador o) { salvos.add(o); }
        @Override public Optional<Organizador> buscarPorId(OrganizadorId id) {
            return salvos.stream().filter(o -> o.getId().equals(id)).findFirst();
        }
        @Override public Optional<Organizador> buscarPorEmail(Email email) {
            return salvos.stream().filter(o -> o.getEmail().equals(email)).findFirst();
        }
        @Override public Optional<Organizador> buscarPorCpf(Cpf cpf) {
            return salvos.stream().filter(o -> o.getCpf().equals(cpf)).findFirst();
        }
        @Override public void remover(OrganizadorId id) { salvos.removeIf(o -> o.getId().equals(id)); }
        @Override public boolean existePorEmail(Email email) {
            return emailsExistentes.contains(email) || salvos.stream().anyMatch(o -> o.getEmail().equals(email));
        }
        @Override public boolean existePorCpf(Cpf cpf) {
            return cpfsExistentes.contains(cpf) || salvos.stream().anyMatch(o -> o.getCpf().equals(cpf));
        }
    }

    private static final class InMemoryParticipanteRepositorio implements ParticipanteRepositorio {
        final List<Participante> salvos = new ArrayList<>();
        @Override public void salvar(Participante p) { salvos.add(p); }
        @Override public Optional<Participante> buscarPorId(ParticipanteId id) {
            return salvos.stream().filter(p -> p.getId().equals(id)).findFirst();
        }
        @Override public Optional<Participante> buscarPorEmail(Email email) {
            return salvos.stream().filter(p -> p.getEmail().equals(email)).findFirst();
        }
        @Override public Optional<Participante> buscarPorCpf(Cpf cpf) {
            return salvos.stream().filter(p -> p.getCpf().equals(cpf)).findFirst();
        }
        @Override public void remover(ParticipanteId id) { salvos.removeIf(p -> p.getId().equals(id)); }
        @Override public boolean existePorEmail(Email email) {
            return salvos.stream().anyMatch(p -> p.getEmail().equals(email));
        }
        @Override public boolean existePorCpf(Cpf cpf) {
            return salvos.stream().anyMatch(p -> p.getCpf().equals(cpf));
        }
        @Override public List<Participante> buscarPorNomeOuEmail(String termo, int limite) {
            return List.of();
        }
    }

    private static final class SetEventosOrganizadorGateway implements EventosOrganizadorGateway {
        final Set<UUID> organizadoresComEventosAtivos = new HashSet<>();
        @Override public boolean possuiEventosAtivos(UUID organizadorId) {
            return organizadoresComEventosAtivos.contains(organizadorId);
        }
    }
}
