package br.voke.dominio.inscricao.inscricao;

import br.voke.dominio.evento.notificacao.IteradorParticipantesElegiveis;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

/**
 * ConcreteIterator do padrão GoF — Iterator.
 *
 * Percorre uma lista de inscrições e retorna apenas os {@code participanteId}
 * cujo status seja {@link StatusInscricao#CONFIRMADA}, implementando a
 * RN 1 (Público-Alvo Restrito) de forma encapsulada.
 *
 * <p>O iterador pré-computa o próximo elemento elegível em {@link #avancar()},
 * garantindo que {@link #hasNext()} seja idempotente e que {@link #next()}
 * lance {@link NoSuchElementException} quando não houver mais elementos.</p>
 */
public class InscricoesAtivasIterador implements IteradorParticipantesElegiveis {

    private final List<Inscricao> inscricoes;
    private int posicaoAtual;
    private UUID proximoElegivel;

    public InscricoesAtivasIterador(List<Inscricao> inscricoes) {
        Objects.requireNonNull(inscricoes, "Lista de inscrições é obrigatória");
        this.inscricoes = inscricoes;
        this.posicaoAtual = 0;
        this.proximoElegivel = null;
        avancar();
    }

    @Override
    public boolean hasNext() {
        return proximoElegivel != null;
    }

    @Override
    public UUID next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Não há mais participantes elegíveis");
        }
        UUID atual = proximoElegivel;
        avancar();
        return atual;
    }

    /**
     * Avança o cursor interno até encontrar a próxima inscrição com
     * status {@link StatusInscricao#CONFIRMADA}, ou marca o fim da iteração.
     */
    private void avancar() {
        proximoElegivel = null;
        while (posicaoAtual < inscricoes.size()) {
            Inscricao inscricao = inscricoes.get(posicaoAtual);
            posicaoAtual++;
            if (inscricao.estaConfirmada()) {
                proximoElegivel = inscricao.getParticipanteId();
                return;
            }
        }
    }
}
