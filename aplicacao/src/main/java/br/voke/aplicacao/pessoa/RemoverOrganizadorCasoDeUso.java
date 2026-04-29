package br.voke.aplicacao.pessoa;

import br.voke.dominio.pessoa.organizador.OrganizadorId;
import br.voke.dominio.pessoa.organizador.OrganizadorServico;

import java.util.Objects;
import java.util.UUID;

public class RemoverOrganizadorCasoDeUso {

    private final OrganizadorServico servico;

    public RemoverOrganizadorCasoDeUso(OrganizadorServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID organizadorId) {
        servico.remover(new OrganizadorId(organizadorId));
    }
}
