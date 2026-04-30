package br.voke.aplicacao.pessoa;

import br.voke.dominio.pessoa.organizador.OrganizadorId;
import br.voke.dominio.pessoa.parceiro.AtividadeParceiro;
import br.voke.dominio.pessoa.parceiro.Parceiro;
import br.voke.dominio.pessoa.parceiro.ParceiroServico;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class CadastrarParceiroCasoDeUso {

    private final ParceiroServico servico;

    public CadastrarParceiroCasoDeUso(ParceiroServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public Parceiro executar(UUID participanteId, UUID organizadorId, Set<AtividadeParceiro> atividades) {
        return servico.cadastrar(
                new ParticipanteId(participanteId),
                new OrganizadorId(organizadorId),
                atividades
        );
    }
}