package br.voke.aplicacao.evento;

import br.voke.dominio.evento.excecao.AcessoGrupoNegadoException;
import br.voke.dominio.evento.grupo.GrupoEvento;
import br.voke.dominio.evento.grupo.GrupoEventoRepositorio;
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

class CriarGrupoEventoCasoDeUsoTest {

    private GrupoEventoRepositorio repositorio;
    private CriarGrupoEventoCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = mock(GrupoEventoRepositorio.class);
        casoDeUso = new CriarGrupoEventoCasoDeUso(repositorio);
    }

    @Test
    void criaGrupoEventoQuandoSolicitanteEhOrganizador() {
        UUID eventoId = UUID.randomUUID();
        UUID organizadorId = UUID.randomUUID();

        GrupoEvento grupo = casoDeUso.executar("Staff Recife Tech",
                "Somente inscritos maiores de idade",
                eventoId, organizadorId, organizadorId);

        assertEquals(eventoId, grupo.getEventoId());
        ArgumentCaptor<GrupoEvento> grupoSalvo = ArgumentCaptor.forClass(GrupoEvento.class);
        verify(repositorio).salvar(grupoSalvo.capture());
        assertEquals("Staff Recife Tech", grupoSalvo.getValue().getNome());
    }

    @Test
    void rejeitaCriacaoQuandoSolicitanteNaoEhOrganizador() {
        UUID organizadorReal = UUID.randomUUID();
        UUID intruso = UUID.randomUUID();

        assertThrows(AcessoGrupoNegadoException.class,
                () -> casoDeUso.executar("Grupo Pirata", "Sem regras",
                        UUID.randomUUID(), organizadorReal, intruso));

        verify(repositorio, never()).salvar(any());
    }
}
