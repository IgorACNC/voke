package br.voke.dominio.evento.grupo;

import br.voke.dominio.evento.excecao.MenorDeIdadeGrupoException;

import java.util.UUID;

/**
 * Decorator concreto — RN1 (Restrição etária de maioridade).
 * Intercepta {@code adicionarMembro} e valida se o participante possui 18 anos
 * ou mais antes de delegar ao próximo componente da cadeia.
 */
public class RestricaoEtariaGrupoDecorator extends GrupoEventoServicoDecorator {

    public RestricaoEtariaGrupoDecorator(GrupoEventoServicoInterface decorado) {
        super(decorado);
    }

    @Override
    public void adicionarMembro(GrupoEventoId grupoId, UUID participanteId,
                                 boolean possuiInscricao, int idadeParticipante) {
        if (idadeParticipante < 18) {
            throw new MenorDeIdadeGrupoException();
        }
        super.adicionarMembro(grupoId, participanteId, possuiInscricao, idadeParticipante);
    }
}
