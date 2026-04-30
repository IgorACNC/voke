package br.voke.aplicacao.pessoa;

import br.voke.dominio.compartilhado.Cpf;
import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.pessoa.excecao.CpfDuplicadoException;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;
import br.voke.dominio.pessoa.participante.ParticipanteServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CadastrarParticipanteCasoDeUsoTest {

    private ParticipanteRepositorio repositorio;
    private CadastrarParticipanteCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = mock(ParticipanteRepositorio.class);
        casoDeUso = new CadastrarParticipanteCasoDeUso(new ParticipanteServico(repositorio));
    }

    @Test
    void cadastraParticipanteAplicandoRegrasDoDominio() {
        Participante participante = casoDeUso.executar("Ana Maria", "52998224725",
                "ana@voke.com", "Senha123", LocalDate.now().minusYears(20));

        assertEquals("ana@voke.com", participante.getEmail().getValor());
        ArgumentCaptor<Participante> participanteSalvo = ArgumentCaptor.forClass(Participante.class);
        verify(repositorio).existePorCpf(new Cpf("52998224725"));
        verify(repositorio).existePorEmail(new Email("ana@voke.com"));
        verify(repositorio).salvar(participanteSalvo.capture());
        assertEquals(participante.getId(), participanteSalvo.getValue().getId());
    }

    @Test
    void rejeitaParticipanteComCpfDuplicado() {
        when(repositorio.existePorCpf(new Cpf("52998224725"))).thenReturn(true);

        assertThrows(CpfDuplicadoException.class, () -> casoDeUso.executar("Ana Maria", "52998224725",
                "ana@voke.com", "Senha123", LocalDate.now().minusYears(20)));

        verify(repositorio, never()).salvar(any());
    }
}
