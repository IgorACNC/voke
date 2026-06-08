package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.sugestao.EventoConsultaGateway;
import br.voke.dominio.fidelidade.sugestao.InscricaoConsultaGateway;
import br.voke.dominio.fidelidade.sugestao.MotorSugestoes;
import br.voke.dominio.fidelidade.sugestao.PreferenciaParticipanteRepositorio;
import br.voke.dominio.fidelidade.sugestao.Sugestao;
import br.voke.dominio.fidelidade.sugestao.SugestaoRepositorio;
import br.voke.dominio.fidelidade.sugestao.SugestaoServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CadastrarSugestaoCasoDeUsoTest {

    private SugestaoRepositorio repositorio;
    private CadastrarSugestaoCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = mock(SugestaoRepositorio.class);
        PreferenciaParticipanteRepositorio prefRepo = mock(PreferenciaParticipanteRepositorio.class);
        EventoConsultaGateway eventoGateway = mock(EventoConsultaGateway.class);
        InscricaoConsultaGateway inscricaoGateway = mock(InscricaoConsultaGateway.class);
        MotorSugestoes motor = new MotorSugestoes(eventoGateway, inscricaoGateway);
        casoDeUso = new CadastrarSugestaoCasoDeUso(
                new SugestaoServico(repositorio, prefRepo, motor, eventoGateway));
    }

    @Test
    void cadastraSugestaoPassandoPeloServicoDeDominio() {
        UUID participanteId = UUID.randomUUID();
        UUID eventoId = UUID.randomUUID();

        Sugestao sugestao = casoDeUso.executar(participanteId, eventoId, "Evento recomendado para voce");

        assertEquals(participanteId, sugestao.getParticipanteId());
        ArgumentCaptor<Sugestao> sugestaoSalva = ArgumentCaptor.forClass(Sugestao.class);
        verify(repositorio).salvar(sugestaoSalva.capture());
        assertEquals("Evento recomendado para voce", sugestaoSalva.getValue().getDescricao());
    }
}
