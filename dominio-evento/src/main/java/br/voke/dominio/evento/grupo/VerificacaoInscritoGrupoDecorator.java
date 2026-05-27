package br.voke.dominio.evento.grupo;

import br.voke.dominio.evento.excecao.AcessoGrupoNegadoException;

import java.util.UUID;

/**
 * Decorator concreto — RN2 (Exclusividade para inscritos).
 * Intercepta {@code adicionarMembro} e valida se o participante possui inscrição
 * confirmada no evento antes de delegar ao próximo componente da cadeia.
 */
public class VerificacaoInscritoGrupoDecorator extends GrupoEventoServicoDecorator {

    public VerificacaoInscritoGrupoDecorator(GrupoEventoServicoInterface decorado) {
        super(decorado);
    }

    @Override
    public void adicionarMembro(GrupoEventoId grupoId, UUID participanteId,
                                 boolean possuiInscricao, int idadeParticipante) {
        if (!possuiInscricao) {
            throw new AcessoGrupoNegadoException("Inscrição necessária para acessar este grupo");
        }
        super.adicionarMembro(grupoId, participanteId, possuiInscricao, idadeParticipante);
    }
}
