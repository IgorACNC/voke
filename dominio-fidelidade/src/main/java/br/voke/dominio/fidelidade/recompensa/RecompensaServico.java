package br.voke.dominio.fidelidade.recompensa;

import br.voke.dominio.fidelidade.carteira.CarteiraVirtualServico;
import br.voke.dominio.fidelidade.pontos.ContaPontos;
import br.voke.dominio.fidelidade.pontos.ContaPontosRepositorio;
import br.voke.dominio.fidelidade.pontos.TipoTransacaoPontos;
import br.voke.dominio.fidelidade.pontos.TransacaoPontos;
import br.voke.dominio.fidelidade.pontos.TransacaoPontosId;
import br.voke.dominio.fidelidade.pontos.TransacaoPontosRepositorio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RecompensaServico {

    private final RecompensaRepositorio repositorio;
    private final ContaPontosRepositorio contaPontosRepositorio;
    private final List<RecompensaObserver> observers;
    private TransacaoPontosRepositorio transacaoPontosRepositorio;
    private CarteiraVirtualServico carteiraServico;

    public RecompensaServico(RecompensaRepositorio repositorio,
                             ContaPontosRepositorio contaPontosRepositorio) {
        Objects.requireNonNull(repositorio, "Repositório de recompensa é obrigatório");
        Objects.requireNonNull(contaPontosRepositorio, "Repositório de conta de pontos é obrigatório");
        this.repositorio = repositorio;
        this.contaPontosRepositorio = contaPontosRepositorio;
        this.observers = new ArrayList<>();
    }

    public void setTransacaoPontosRepositorio(TransacaoPontosRepositorio transacaoPontosRepositorio) {
        this.transacaoPontosRepositorio = transacaoPontosRepositorio;
    }

    public void setCarteiraServico(CarteiraVirtualServico carteiraServico) {
        this.carteiraServico = carteiraServico;
    }

    public void adicionarObserver(RecompensaObserver observer) {
        Objects.requireNonNull(observer, "Observer não pode ser nulo");
        this.observers.add(observer);
    }

    public void removerObserver(RecompensaObserver observer) {
        this.observers.remove(observer);
    }

    public List<RecompensaObserver> getObservers() {
        return Collections.unmodifiableList(observers);
    }

    public Recompensa cadastrar(String nome, String descricao, int custoEmPontos,
                                int estoqueTotal, UUID organizadorId) {
        return cadastrar(nome, descricao, custoEmPontos, estoqueTotal, organizadorId, CategoriaRecompensa.DESCONTO);
    }

    public Recompensa cadastrar(String nome, String descricao, int custoEmPontos,
                                int estoqueTotal, UUID organizadorId, CategoriaRecompensa categoria) {
        return cadastrar(nome, descricao, custoEmPontos, estoqueTotal, organizadorId, categoria, null);
    }

    public Recompensa cadastrar(String nome, String descricao, int custoEmPontos,
                                int estoqueTotal, UUID organizadorId, CategoriaRecompensa categoria,
                                java.math.BigDecimal valor) {
        Recompensa recompensa = new Recompensa(
                RecompensaId.novo(), nome, descricao, custoEmPontos, estoqueTotal,
                organizadorId, categoria, valor);
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

        // RN5 — efeito real da recompensa por categoria
        executarEfeitoCategoria(recompensa, participanteId);

        if (transacaoPontosRepositorio != null) {
            transacaoPontosRepositorio.salvar(new TransacaoPontos(
                    TransacaoPontosId.novo(), participanteId, TipoTransacaoPontos.RESGATE_RECOMPENSA,
                    recompensa.getCustoEmPontos(),
                    "Resgate: " + recompensa.getNome(),
                    recompensa.getId().getValor()));
        }

        notificarResgatada(recompensa, participanteId);

        if (!recompensa.estaDisponivel()) {
            notificarEsgotada(recompensa);
        }
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

    public List<Recompensa> listarAtivas() {
        return repositorio.buscarAtivas();
    }

    private void executarEfeitoCategoria(Recompensa recompensa, UUID participanteId) {
        switch (recompensa.getCategoria()) {
            case CREDITO_CARTEIRA -> {
                if (carteiraServico != null && recompensa.getValor() != null
                        && recompensa.getValor().compareTo(java.math.BigDecimal.ZERO) > 0) {
                    carteiraServico.creditar(participanteId, recompensa.getValor());
                }
            }
            case CUPOM, DESCONTO, BRINDE, BENEFICIO -> {
                // CUPOM: código entregue ao participante para uso nos eventos elegíveis.
                // DESCONTO/BRINDE/BENEFICIO: legados — sem efeito automático.
            }
        }
    }

    private void notificarResgatada(Recompensa recompensa, UUID participanteId) {
        for (RecompensaObserver observer : observers) {
            observer.onRecompensaResgatada(recompensa, participanteId);
        }
    }

    private void notificarEsgotada(Recompensa recompensa) {
        for (RecompensaObserver observer : observers) {
            observer.onRecompensaEsgotada(recompensa);
        }
    }
}
