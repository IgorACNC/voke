package br.voke.aplicacao.pessoa;

import br.voke.dominio.compartilhado.Cpf;
import br.voke.dominio.compartilhado.DataNascimento;
import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.compartilhado.Senha;
import br.voke.dominio.pessoa.amizade.Amizade;
import br.voke.dominio.pessoa.amizade.AmizadeId;
import br.voke.dominio.pessoa.amizade.AmizadeRepositorio;
import br.voke.dominio.pessoa.amizade.AmizadeServico;
import br.voke.dominio.pessoa.excecao.AmizadeJaExisteException;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SolicitarAmizadeCasoDeUsoTest {

    private InMemoryParticipanteRepositorio participanteRepositorio;
    private InMemoryAmizadeRepositorio amizadeRepositorio;
    private SolicitarAmizadeCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        participanteRepositorio = new InMemoryParticipanteRepositorio();
        amizadeRepositorio = new InMemoryAmizadeRepositorio();
        casoDeUso = new SolicitarAmizadeCasoDeUso(new AmizadeServico(amizadeRepositorio), participanteRepositorio);
    }

    @Test
    void buscaParticipanteSolicitanteEAplicaRegraDeCriacaoDaAmizade() {
        Participante solicitante = participante("Ana Maria", "ana@voke.com");
        participanteRepositorio.salvar(solicitante);
        UUID receptorId = UUID.randomUUID();

        casoDeUso.executar(solicitante.getId().getValor(), receptorId);

        assertEquals(1, amizadeRepositorio.amizades.size());
    }

    @Test
    void rejeitaSolicitacaoQuandoParticipanteNaoExiste() {
        assertThrows(IllegalArgumentException.class,
                () -> casoDeUso.executar(UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    void rejeitaSolicitacaoDuplicadaUsandoServicoDeDominio() {
        Participante solicitante = participante("Ana Maria", "ana@voke.com");
        participanteRepositorio.salvar(solicitante);
        amizadeRepositorio.existeEntreParticipantes = true;

        assertThrows(AmizadeJaExisteException.class,
                () -> casoDeUso.executar(solicitanteId(solicitante), UUID.randomUUID()));
    }

    private UUID solicitanteId(Participante participante) {
        return participante.getId().getValor();
    }

    private Participante participante(String nome, String email) {
        return new Participante(ParticipanteId.novo(), new NomeCompleto(nome),
                new Cpf("52998224725"), new Email(email), new Senha("Senha123"),
                new DataNascimento(LocalDate.now().minusYears(20)));
    }

    private static final class InMemoryParticipanteRepositorio implements ParticipanteRepositorio {
        private final Map<ParticipanteId, Participante> participantes = new HashMap<>();

        @Override
        public void salvar(Participante participante) {
            participantes.put(participante.getId(), participante);
        }

        @Override
        public Optional<Participante> buscarPorId(ParticipanteId id) {
            return Optional.ofNullable(participantes.get(id));
        }

        @Override
        public Optional<Participante> buscarPorEmail(Email email) {
            return participantes.values().stream().filter(participante -> participante.getEmail().equals(email)).findFirst();
        }

        @Override
        public Optional<Participante> buscarPorCpf(Cpf cpf) {
            return participantes.values().stream().filter(participante -> participante.getCpf().equals(cpf)).findFirst();
        }

        @Override
        public void remover(ParticipanteId id) {
            participantes.remove(id);
        }

        @Override
        public boolean existePorEmail(Email email) {
            return buscarPorEmail(email).isPresent();
        }

        @Override
        public boolean existePorCpf(Cpf cpf) {
            return buscarPorCpf(cpf).isPresent();
        }
    }

    private static final class InMemoryAmizadeRepositorio implements AmizadeRepositorio {
        private final List<Amizade> amizades = new ArrayList<>();
        private boolean existeEntreParticipantes;

        @Override
        public void salvar(Amizade amizade) {
            amizades.add(amizade);
        }

        @Override
        public Optional<Amizade> buscarPorId(AmizadeId id) {
            return amizades.stream().filter(amizade -> amizade.getId().equals(id)).findFirst();
        }

        @Override
        public List<Amizade> buscarPorParticipante(ParticipanteId participanteId) {
            return amizades.stream()
                    .filter(amizade -> amizade.getSolicitanteId().equals(participanteId)
                            || amizade.getReceptorId().equals(participanteId))
                    .toList();
        }

        @Override
        public List<Amizade> buscarAtivasPorParticipante(ParticipanteId participanteId) {
            return buscarPorParticipante(participanteId).stream().filter(Amizade::estaAtiva).toList();
        }

        @Override
        public boolean existeEntreParticipantes(ParticipanteId participanteA, ParticipanteId participanteB) {
            return existeEntreParticipantes;
        }

        @Override
        public void remover(AmizadeId id) {
            amizades.removeIf(amizade -> amizade.getId().equals(id));
        }
    }
}
