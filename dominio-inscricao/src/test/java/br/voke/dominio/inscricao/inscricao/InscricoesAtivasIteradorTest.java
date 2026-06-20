package br.voke.dominio.inscricao.inscricao;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InscricoesAtivasIteradorTest {

    private static final UUID EVENTO_ID = UUID.randomUUID();

    private Inscricao criarInscricao(UUID participanteId) {
        return new Inscricao(InscricaoId.novo(), participanteId, EVENTO_ID, BigDecimal.TEN);
    }

    @Test
    void retornaApenasParticipantesComInscricaoConfirmada() {
        UUID participante1 = UUID.randomUUID();
        UUID participante2 = UUID.randomUUID();
        UUID participante3 = UUID.randomUUID();

        Inscricao confirmada1 = criarInscricao(participante1);
        Inscricao cancelada = criarInscricao(participante2);
        cancelada.cancelar();
        Inscricao confirmada2 = criarInscricao(participante3);

        InscricoesAtivasAgregado agregado = new InscricoesAtivasAgregado(
                List.of(confirmada1, cancelada, confirmada2));

        Iterator<UUID> iterator = agregado.iterator();

        assertTrue(iterator.hasNext());
        assertEquals(participante1, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(participante3, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void excluiInscricoesComCheckInRealizado() {
        UUID participante1 = UUID.randomUUID();
        UUID participante2 = UUID.randomUUID();

        Inscricao comCheckIn = criarInscricao(participante1);
        comCheckIn.realizarCheckIn();
        Inscricao confirmada = criarInscricao(participante2);

        InscricoesAtivasAgregado agregado = new InscricoesAtivasAgregado(
                List.of(comCheckIn, confirmada));

        Iterator<UUID> iterator = agregado.iterator();

        assertTrue(iterator.hasNext());
        assertEquals(participante2, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void retornaVazioQuandoTodasCanceladas() {
        UUID participante1 = UUID.randomUUID();
        UUID participante2 = UUID.randomUUID();

        Inscricao cancelada1 = criarInscricao(participante1);
        cancelada1.cancelar();
        Inscricao cancelada2 = criarInscricao(participante2);
        cancelada2.cancelar();

        InscricoesAtivasAgregado agregado = new InscricoesAtivasAgregado(
                List.of(cancelada1, cancelada2));

        Iterator<UUID> iterator = agregado.iterator();

        assertFalse(iterator.hasNext());
    }

    @Test
    void retornaVazioParaListaVazia() {
        InscricoesAtivasAgregado agregado = new InscricoesAtivasAgregado(List.of());

        Iterator<UUID> iterator = agregado.iterator();

        assertFalse(iterator.hasNext());
    }

    @Test
    void lancaExcecaoQuandoNaoHaMaisElementos() {
        UUID participante = UUID.randomUUID();
        Inscricao confirmada = criarInscricao(participante);

        InscricoesAtivasAgregado agregado = new InscricoesAtivasAgregado(List.of(confirmada));

        Iterator<UUID> iterator = agregado.iterator();
        iterator.next(); // consome o único elemento

        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void hasNextEhIdempotente() {
        UUID participante = UUID.randomUUID();
        Inscricao confirmada = criarInscricao(participante);

        InscricoesAtivasAgregado agregado = new InscricoesAtivasAgregado(List.of(confirmada));

        Iterator<UUID> iterator = agregado.iterator();

        // Chamar hasNext múltiplas vezes não avança o cursor
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertEquals(participante, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void suportaForEachViaPadraoIterable() {
        UUID participante1 = UUID.randomUUID();
        UUID participante2 = UUID.randomUUID();

        Inscricao confirmada1 = criarInscricao(participante1);
        Inscricao cancelada = criarInscricao(UUID.randomUUID());
        cancelada.cancelar();
        Inscricao confirmada2 = criarInscricao(participante2);

        InscricoesAtivasAgregado agregado = new InscricoesAtivasAgregado(
                List.of(confirmada1, cancelada, confirmada2));

        List<UUID> coletados = new ArrayList<>();
        for (UUID id : agregado) {
            coletados.add(id);
        }

        assertEquals(2, coletados.size());
        assertEquals(participante1, coletados.get(0));
        assertEquals(participante2, coletados.get(1));
    }

    @Test
    void cadaChamadaDeIteratorCriaNovoIterador() {
        UUID participante = UUID.randomUUID();
        Inscricao confirmada = criarInscricao(participante);

        InscricoesAtivasAgregado agregado = new InscricoesAtivasAgregado(List.of(confirmada));

        // Primeiro iterador - consome completamente
        Iterator<UUID> iter1 = agregado.iterator();
        assertEquals(participante, iter1.next());
        assertFalse(iter1.hasNext());

        // Segundo iterador - deve começar do início
        Iterator<UUID> iter2 = agregado.iterator();
        assertTrue(iter2.hasNext());
        assertEquals(participante, iter2.next());
    }
}
