package br.voke.aplicacao.evento;

import br.voke.dominio.evento.categoria.CategoriaId;
import br.voke.dominio.evento.categoria.CategoriaRepositorio;
import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoServico;
import br.voke.dominio.evento.evento.Lote;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class CriarEventoCasoDeUso {

    private final EventoServico servico;
    private final CategoriaRepositorio categoriaRepositorio;

    public CriarEventoCasoDeUso(EventoServico servico, CategoriaRepositorio categoriaRepositorio) {
        Objects.requireNonNull(servico);
        Objects.requireNonNull(categoriaRepositorio);
        this.servico = servico;
        this.categoriaRepositorio = categoriaRepositorio;
    }

    public Evento executar(String nome, String descricao, String local,
                           LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim,
                           int capacidadeMaxima, UUID organizadorId,
                           BigDecimal precoLote, int quantidadeLote, int idadeMinima,
                           Set<UUID> categoriaIds) {
        validarCategorias(categoriaIds);
        Lote loteInicial = new Lote(1, precoLote, quantidadeLote);
        return servico.criar(nome, descricao, local, dataHoraInicio, dataHoraFim,
                capacidadeMaxima, organizadorId, loteInicial, idadeMinima,
                categoriaIds != null ? categoriaIds : Set.of());
    }

    private void validarCategorias(Set<UUID> categoriaIds) {
        if (categoriaIds == null || categoriaIds.isEmpty()) {
            throw new IllegalArgumentException("Selecione ao menos uma categoria para o evento");
        }
        for (UUID id : categoriaIds) {
            if (categoriaRepositorio.buscarPorId(new CategoriaId(id)).isEmpty()) {
                throw new IllegalArgumentException("Categoria inválida: " + id);
            }
        }
    }
}
