package br.voke.dominio.inscricao.inscricao;

import br.voke.dominio.evento.notificacao.ParticipanteElegivel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * ConcreteAggregate do padrão GoF — Iterator.
 *
 * Encapsula uma coleção de {@link Inscricao} e fornece um factory method
 * {@link #iterator()} que retorna um {@link InscricoesAtivasIterador},
 * garantindo que apenas participantes com inscrição {@link StatusInscricao#CONFIRMADA}
 * sejam percorridos.
 *
 * <p>Esta classe blinda o mecanismo de disparo de notificações contra
 * detalhes de estruturas de listas ou consultas ao banco de dados,
 * aderindo ao princípio de encapsulamento do padrão Iterator.</p>
 */
public class InscricoesAtivasAgregado implements ParticipanteElegivel {

    private final List<Inscricao> inscricoes;

    public InscricoesAtivasAgregado(List<Inscricao> inscricoes) {
        Objects.requireNonNull(inscricoes, "Lista de inscrições é obrigatória");
        this.inscricoes = Collections.unmodifiableList(new ArrayList<>(inscricoes));
    }

    /**
     * Cria um novo iterador que percorre apenas os participantes
     * com inscrição ativa (CONFIRMADA) no evento.
     *
     * @return novo {@link InscricoesAtivasIterador}
     */
    @Override
    public Iterator<UUID> iterator() {
        return new InscricoesAtivasIterador(inscricoes);
    }
}
