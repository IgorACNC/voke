package br.voke.aplicacao.evento;

import br.voke.dominio.evento.grupo.GrupoEvento;
import br.voke.dominio.evento.grupo.GrupoEventoRepositorio;
import br.voke.dominio.evento.grupo.GrupoEventoServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CriarGrupoEventoCasoDeUsoTest {

    private GrupoEventoRepositorio repositorio;
    private CriarGrupoEventoCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = mock(GrupoEventoRepositorio.class);
        casoDeUso = new CriarGrupoEventoCasoDeUso(new GrupoEventoServico(repositorio));
    }

    @Test
    void criaGrupoEventoPassandoPeloServicoDeDominio() {
        UUID eventoId = UUID.randomUUID();
        UUID organizadorId = UUID.randomUUID();

        GrupoEvento grupo = casoDeUso.executar("Staff Recife Tech", "Somente inscritos maiores de idade",
                eventoId, organizadorId);

        assertEquals(eventoId, grupo.getEventoId());
        ArgumentCaptor<GrupoEvento> grupoSalvo = ArgumentCaptor.forClass(GrupoEvento.class);
        verify(repositorio).salvar(grupoSalvo.capture());
        assertEquals("Staff Recife Tech", grupoSalvo.getValue().getNome());
    }
}
