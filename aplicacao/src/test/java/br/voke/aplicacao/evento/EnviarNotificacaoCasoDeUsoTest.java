package br.voke.aplicacao.evento;

import br.voke.dominio.evento.excecao.EventoCanceladoException;
import br.voke.dominio.evento.notificacao.Notificacao;
import br.voke.dominio.evento.notificacao.NotificacaoRepositorio;
import br.voke.dominio.evento.notificacao.NotificacaoServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class EnviarNotificacaoCasoDeUsoTest {

    private NotificacaoRepositorio repositorio;
    private EnviarNotificacaoCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = mock(NotificacaoRepositorio.class);
        casoDeUso = new EnviarNotificacaoCasoDeUso(new NotificacaoServico(repositorio));
    }

    @Test
    void enviaNotificacaoParaEventoAtivo() {
        UUID eventoId = UUID.randomUUID();

        Notificacao notificacao = casoDeUso.executar(eventoId, "Portoes abrem as 18h", true);

        assertEquals(eventoId, notificacao.getEventoId());
        ArgumentCaptor<Notificacao> notificacaoSalva = ArgumentCaptor.forClass(Notificacao.class);
        verify(repositorio).salvar(notificacaoSalva.capture());
        assertEquals("Portoes abrem as 18h", notificacaoSalva.getValue().getConteudo());
    }

    @Test
    void rejeitaNotificacaoParaEventoCancelado() {
        assertThrows(EventoCanceladoException.class,
                () -> casoDeUso.executar(UUID.randomUUID(), "Portoes abrem as 18h", false));

        verify(repositorio, never()).salvar(any());
    }
}