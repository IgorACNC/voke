package br.voke.aplicacao.evento;

import br.voke.dominio.evento.grupo.GrupoEvento;
import br.voke.dominio.evento.grupo.GrupoEventoRepositorio;
import br.voke.dominio.evento.grupo.GrupoEventoServico;
import br.voke.dominio.evento.grupo.GrupoEventoServicoInterface;
import br.voke.dominio.evento.grupo.PrivilegioOrganizadorGrupoDecorator;
import br.voke.dominio.evento.grupo.RestricaoEtariaGrupoDecorator;
import br.voke.dominio.evento.grupo.VerificacaoInscritoGrupoDecorator;

import java.util.Objects;
import java.util.UUID;

public class CriarGrupoEventoCasoDeUso {

    private final GrupoEventoServicoInterface servico;

    public CriarGrupoEventoCasoDeUso(GrupoEventoRepositorio repositorio) {
        Objects.requireNonNull(repositorio);
        GrupoEventoServico servicoBase = new GrupoEventoServico(repositorio);
        this.servico = new RestricaoEtariaGrupoDecorator(
                new VerificacaoInscritoGrupoDecorator(
                        new PrivilegioOrganizadorGrupoDecorator(servicoBase, repositorio)
                )
        );
    }

    public GrupoEvento executar(String nome, String regras, UUID eventoId, UUID organizadorId,
                                UUID solicitanteId) {
        return servico.criar(nome, regras, eventoId, organizadorId, solicitanteId);
    }
}
