package br.voke.aplicacao.evento;

import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoServico;
import br.voke.dominio.evento.evento.Lote;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class CriarEventoCasoDeUso {

    private final EventoServico servico;

    public CriarEventoCasoDeUso(EventoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public Evento executar(String nome, String descricao, String local,
                           LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim,
                           int capacidadeMaxima, UUID organizadorId,
                           BigDecimal precoLote, int quantidadeLote, int idadeMinima) {
        Lote loteInicial = new Lote(1, precoLote, quantidadeLote);
        return servico.criar(nome, descricao, local, dataHoraInicio, dataHoraFim,
                capacidadeMaxima, organizadorId, loteInicial, idadeMinima);
    }
}
