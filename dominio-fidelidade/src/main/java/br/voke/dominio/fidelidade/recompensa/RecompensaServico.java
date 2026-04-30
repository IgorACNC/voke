package br.voke.dominio.fidelidade.recompensa;

import br.voke.dominio.fidelidade.pontos.ContaPontos;
import br.voke.dominio.fidelidade.pontos.ContaPontosRepositorio;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RecompensaServico {

    private final RecompensaRepositorio repositorio;
    private final ContaPontosRepositorio contaPontosRepositorio;

    public RecompensaServico(RecompensaRepositorio repositorio,
                             ContaPontosRepositorio contaPontosRepositorio) {
        Objects.requireNonNull(repositorio, "Repositório de recompensa é obrigatório");
        Objects.requireNonNull(contaPontosRepositorio, "Repositório de conta de pontos é obrigatório");
        this.repositorio = repositorio;
        this.contaPontosRepositorio = contaPontosRepositorio;
    }

    public Recompensa cadastrar(String nome, String descricao, int custoEmPontos,
                                int estoqueTotal, UUID organizadorId) {
        Recompensa recompensa = new Recompensa(
                RecompensaId.novo(), nome, descricao, custoEmPontos, estoqueTotal, organizadorId);
        repositorio.salvar(recompensa);
        return recompensa;
    }

    public void atualizarDescricao(RecompensaId id, String novaDescricao) {
        Recompensa recompensa = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Recompensa não encontrada"));
        recompensa.atualizarDescricao(novaDescricao);
        repositorio.salvar(recompensa);
    }

    public void alterarCusto(RecompensaId id, int novoCusto) {
        Recompensa recompensa = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Recompensa não encontrada"));
        recompensa.alterarCusto(novoCusto);
        repositorio.salvar(recompensa);
    }

    public void resgatar(UUID participanteId, RecompensaId recompensaId) {
        ContaPontos conta = contaPontosRepositorio.buscarPorParticipanteId(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Conta de pontos não encontrada"));
        Recompensa recompensa = repositorio.buscarPorId(recompensaId)
                .orElseThrow(() -> new IllegalArgumentException("Recompensa não encontrada"));
        conta.debitar(recompensa.getCustoEmPontos());
        recompensa.resgatar();
        contaPontosRepositorio.salvar(conta);
        repositorio.salvar(recompensa);
    }

    public void inativar(RecompensaId id) {
        Recompensa recompensa = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Recompensa não encontrada"));
        recompensa.inativar();
        repositorio.salvar(recompensa);
    }

    public void remover(RecompensaId id) {
        repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Recompensa não encontrada"));
        repositorio.remover(id);
    }

    public List<Recompensa> listarPorOrganizador(UUID organizadorId) {
        return repositorio.buscarPorOrganizadorId(organizadorId);
    }
}
