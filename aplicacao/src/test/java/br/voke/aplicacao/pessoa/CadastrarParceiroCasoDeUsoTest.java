package br.voke.aplicacao.pessoa;

import br.voke.dominio.pessoa.excecao.PresencaInsuficienteException;
import br.voke.dominio.pessoa.organizador.OrganizadorId;
import br.voke.dominio.pessoa.parceiro.AtividadeParceiro;
import br.voke.dominio.pessoa.parceiro.Parceiro;
import br.voke.dominio.pessoa.parceiro.ParceiroRepositorio;
import br.voke.dominio.pessoa.parceiro.ParceiroServico;
import br.voke.dominio.pessoa.parceiro.PresencaConsulta;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CadastrarParceiroCasoDeUsoTest {

    private ParceiroRepositorio repositorio;
    private PresencaConsulta presencaConsulta;
    private CadastrarParceiroCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = mock(ParceiroRepositorio.class);
        presencaConsulta = mock(PresencaConsulta.class);
        casoDeUso = new CadastrarParceiroCasoDeUso(new ParceiroServico(repositorio, presencaConsulta));
    }

    @Test
    void cadastraParceiroQuandoParticipantePossuiPresencaMinima() {
        UUID participanteId = UUID.randomUUID();
        UUID organizadorId = UUID.randomUUID();
        when(presencaConsulta.contarEventosConfirmados(new ParticipanteId(participanteId), new OrganizadorId(organizadorId)))
                .thenReturn(5);

        Parceiro parceiro = casoDeUso.executar(participanteId, organizadorId,
                Set.of(AtividadeParceiro.DIVULGACAO_REDES_SOCIAIS));

        assertEquals(new ParticipanteId(participanteId), parceiro.getParticipanteId());
        ArgumentCaptor<Parceiro> parceiroSalvo = ArgumentCaptor.forClass(Parceiro.class);
        verify(repositorio).salvar(parceiroSalvo.capture());
        assertEquals(new OrganizadorId(organizadorId), parceiroSalvo.getValue().getOrganizadorId());
    }

    @Test
    void rejeitaParceiroComPresencaInsuficiente() {
        when(presencaConsulta.contarEventosConfirmados(any(), any())).thenReturn(4);

        assertThrows(PresencaInsuficienteException.class, () -> casoDeUso.executar(UUID.randomUUID(), UUID.randomUUID(),
                Set.of(AtividadeParceiro.DIVULGACAO_REDES_SOCIAIS)));

        verify(repositorio, never()).salvar(any());
    }
}
