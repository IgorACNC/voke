package br.voke.aplicacao.evento;

import br.voke.dominio.evento.excecao.FavoritoDuplicadoException;
import br.voke.dominio.evento.favorito.Favorito;
import br.voke.dominio.evento.favorito.FavoritoRepositorio;
import br.voke.dominio.evento.favorito.FavoritoServico;
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
import static org.mockito.Mockito.when;

class AdicionarFavoritoCasoDeUsoTest {

    private FavoritoRepositorio repositorio;
    private AdicionarFavoritoCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = mock(FavoritoRepositorio.class);
        casoDeUso = new AdicionarFavoritoCasoDeUso(new FavoritoServico(repositorio));
    }

    @Test
    void adicionaFavoritoParaEventoPublicado() {
        UUID participanteId = UUID.randomUUID();
        UUID eventoId = UUID.randomUUID();

        Favorito favorito = casoDeUso.executar(participanteId, eventoId, "PUBLICADO");

        assertEquals(participanteId, favorito.getParticipanteId());
        ArgumentCaptor<Favorito> favoritoSalvo = ArgumentCaptor.forClass(Favorito.class);
        verify(repositorio).existePorParticipanteEEvento(participanteId, eventoId);
        verify(repositorio).salvar(favoritoSalvo.capture());
        assertEquals(eventoId, favoritoSalvo.getValue().getEventoId());
    }

    @Test
    void rejeitaFavoritoDuplicado() {
        UUID participanteId = UUID.randomUUID();
        UUID eventoId = UUID.randomUUID();
        when(repositorio.existePorParticipanteEEvento(participanteId, eventoId)).thenReturn(true);

        assertThrows(FavoritoDuplicadoException.class,
                () -> casoDeUso.executar(participanteId, eventoId, "PUBLICADO"));

        verify(repositorio, never()).salvar(any());
    }
}