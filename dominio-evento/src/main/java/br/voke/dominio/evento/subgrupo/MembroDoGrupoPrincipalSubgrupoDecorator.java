package br.voke.dominio.evento.subgrupo;

import java.util.UUID;

/**
 * Decorator que garante que apenas membros do grupo principal do evento
 * podem entrar em subgrupos.
 */
public class MembroDoGrupoPrincipalSubgrupoDecorator extends SubgrupoServicoDecorator {

    public MembroDoGrupoPrincipalSubgrupoDecorator(SubgrupoServicoInterface decorado) {
        super(decorado);
    }

    @Override
    public void adicionarMembro(SubgrupoId subgrupoId, UUID participanteId, UUID solicitanteId,
                                 boolean ehMembroDoGrupoPrincipal, boolean solicitanteEhGestor) {
        if (!ehMembroDoGrupoPrincipal) {
            throw new MembroNaoEstaNoGrupoPrincipalException();
        }
        super.adicionarMembro(subgrupoId, participanteId, solicitanteId,
                ehMembroDoGrupoPrincipal, solicitanteEhGestor);
    }
}
