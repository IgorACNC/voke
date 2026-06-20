package br.voke.aplicacao.evento;

import br.voke.dominio.evento.subgrupo.CategoriaSubgrupo;
import br.voke.dominio.evento.subgrupo.Subgrupo;
import br.voke.dominio.evento.subgrupo.SubgrupoServicoInterface;
import br.voke.dominio.evento.subgrupo.TipoSubgrupo;

import java.util.Objects;
import java.util.UUID;

public class CriarSubgrupoCasoDeUso {

    private final SubgrupoServicoInterface servico;

    public CriarSubgrupoCasoDeUso(SubgrupoServicoInterface servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public Subgrupo executar(String nome, String descricao, String regras, UUID grupoEventoId,
                              CategoriaSubgrupo categoria, TipoSubgrupo tipo, int limiteMembros,
                              UUID solicitanteId, boolean solicitanteEhOrganizador) {
        return servico.criar(nome, descricao, regras, grupoEventoId, categoria, tipo,
                limiteMembros, solicitanteId, solicitanteEhOrganizador);
    }
}
