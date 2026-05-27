package br.voke.aplicacao.evento;

import br.voke.dominio.evento.grupo.GrupoEvento;
import br.voke.dominio.evento.grupo.GrupoEventoServicoInterface;

import java.util.Objects;
import java.util.UUID;

public class CriarGrupoEventoCasoDeUso {

    private final GrupoEventoServicoInterface servico;

    public CriarGrupoEventoCasoDeUso(GrupoEventoServicoInterface servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public GrupoEvento executar(String nome, String regras, UUID eventoId, UUID organizadorId) {
        return servico.criar(nome, regras, eventoId, organizadorId);
    }
}
