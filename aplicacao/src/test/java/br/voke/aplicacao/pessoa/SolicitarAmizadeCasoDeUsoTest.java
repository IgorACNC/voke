package br.voke.aplicacao.pessoa;

import br.voke.dominio.compartilhado.Cpf;
import br.voke.dominio.compartilhado.DataNascimento;
import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.compartilhado.Senha;
import br.voke.dominio.pessoa.amizade.Amizade;
import br.voke.dominio.pessoa.amizade.AmizadeRepositorio;
import br.voke.dominio.pessoa.amizade.AmizadeServico;
import br.voke.dominio.pessoa.excecao.AmizadeJaExisteException;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SolicitarAmizadeCasoDeUsoTest {

    private ParticipanteRepositorio participanteRepositorio;
    private AmizadeRepositorio amizadeRepositorio;
    private SolicitarAmizadeCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        participanteRepositorio = mock(ParticipanteRepositorio.class);
        amizadeRepositorio = mock(AmizadeRepositorio.class);
        casoDeUso = new SolicitarAmizadeCasoDeUso(new AmizadeServico(amizadeRepositorio), participanteRepositorio);
    }

    @Test
    void buscaParticipanteSolicitanteEAplicaRegraDeCriacaoDaAmizade() {
        Participante solicitante = participante("Ana Maria", "ana@voke.com");
        UUID receptorId = UUID.randomUUID();
        when(participanteRepositorio.buscarPorId(solicitante.getId())).thenReturn(Optional.of(solicitante));

        casoDeUso.executar(solicitante.getId().getValor(), receptorId);

        ArgumentCaptor<Amizade> amizadeSalva = ArgumentCaptor.forClass(Amizade.class);
        verify(amizadeRepositorio).existeEntreParticipantes(solicitante.getId(), new br.voke.dominio.pessoa.participante.ParticipanteId(receptorId));
        verify(amizadeRepositorio).salvar(amizadeSalva.capture());
        assertEquals(solicitante.getId(), amizadeSalva.getValue().getSolicitanteId());
        assertEquals(new ParticipanteId(receptorId), amizadeSalva.getValue().getReceptorId());
    }

    @Test
    void rejeitaSolicitacaoQuandoParticipanteNaoExiste() {
        assertThrows(IllegalArgumentException.class,
                () -> casoDeUso.executar(UUID.randomUUID(), UUID.randomUUID()));

        verify(amizadeRepositorio, never()).salvar(any());
    }

    @Test
    void rejeitaSolicitacaoDuplicadaUsandoServicoDeDominio() {
        Participante solicitante = participante("Ana Maria", "ana@voke.com");
        UUID receptorId = UUID.randomUUID();
        when(participanteRepositorio.buscarPorId(solicitante.getId())).thenReturn(Optional.of(solicitante));
        when(amizadeRepositorio.existeEntreParticipantes(solicitante.getId(), new ParticipanteId(receptorId)))
                .thenReturn(true);

        assertThrows(AmizadeJaExisteException.class,
                () -> casoDeUso.executar(solicitante.getId().getValor(), receptorId));

        verify(amizadeRepositorio, never()).salvar(any());
    }

    private Participante participante(String nome, String email) {
        return new Participante(ParticipanteId.novo(), new NomeCompleto(nome),
                new Cpf("52998224725"), new Email(email), new Senha("Senha123"),
                new DataNascimento(LocalDate.now().minusYears(20)));
    }
}
