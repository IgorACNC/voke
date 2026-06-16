package br.voke.dominio.pessoa.organizador;

import java.util.UUID;

public interface EventosOrganizadorGateway {
    boolean possuiEventosAtivos(UUID organizadorId);
}
