package br.voke.aplicacao.pessoa;

import br.voke.dominio.pessoa.organizador.OrganizadorId;
import br.voke.dominio.pessoa.parceiro.Parceiro;
import br.voke.dominio.pessoa.parceiro.ParceiroServico;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListarParceirosOrganizadorCasoDeUso {

    private final ParceiroServico servico;

    public ListarParceirosOrganizadorCasoDeUso(ParceiroServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public List<Parceiro> executar(UUID organizadorId) {
        return servico.listarPorOrganizador(new OrganizadorId(organizadorId));
    }
}
