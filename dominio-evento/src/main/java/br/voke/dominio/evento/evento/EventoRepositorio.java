package br.voke.dominio.evento.evento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventoRepositorio {
    void salvar(Evento evento);
    Optional<Evento> buscarPorId(EventoId id);
    Optional<Evento> buscarPorNome(String nome);
    List<Evento> buscarPorLocalEPeriodo(String local, LocalDateTime inicio, LocalDateTime fim);
    void remover(EventoId id);
    boolean existePorNome(String nome);
}
