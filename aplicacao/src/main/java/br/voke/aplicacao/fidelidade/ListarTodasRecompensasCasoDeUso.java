package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.recompensa.Recompensa;
import br.voke.dominio.fidelidade.recompensa.RecompensaRepositorio;

import java.util.List;
import java.util.Objects;

public class ListarTodasRecompensasCasoDeUso {

    private final RecompensaRepositorio repositorio;

    public ListarTodasRecompensasCasoDeUso(RecompensaRepositorio repositorio) {
        Objects.requireNonNull(repositorio);
        this.repositorio = repositorio;
    }

    public List<Recompensa> executar() {
        return repositorio.buscarTodas();
    }
}
